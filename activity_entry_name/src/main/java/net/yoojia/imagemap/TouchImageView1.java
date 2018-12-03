package net.yoojia.imagemap;

import net.yoojia.imagemap.animator.MapHandle;
import net.yoojia.imagemap.animator.MapHandleImp;
import net.yoojia.imagemap.event.RotateGestureDetector;
import net.yoojia.imagemap.event.RotateGestureDetector.SimpleOnRotateGestureListener;
import net.yoojia.imagemap.util.BaZhanUtil;
import net.yoojia.imagemap.util.ImageViewHelper;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;

/**
 * TouchImageView设计为一个具有独立完整功能的View。可缩放，拖动图片。 TouchImageView - A full View with
 * Scale/Drag support.
 */
@SuppressWarnings("deprecation")
public class TouchImageView1 extends ImageView implements
		OnGlobalLayoutListener, MapHandle {
	private final Matrix imageUsingMatrix = new Matrix();
	private final ImageViewHelper mImageViewHelper = new ImageViewHelper();
	private final float[] matrixValues = new float[9];

	/* 惯性系数 */
	private static final float FRICTION = 0.0f;

	private PointF mid = new PointF();

	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateGestureDetector;
	private GestureDetector mGestureDetector;

	private boolean isAllowRotate = true;
	protected boolean isAllowTranslate = false;

	protected int mView_width;
	protected int mView_height;
	private boolean isNewImageSVG = true;
	protected MapHandleImp mMapHandle;
	private BaZhanUtil mBaZhanUtil;

	private boolean canChange=true;

	public TouchImageView1(Context context) {
		this(context, null);
	}

	public TouchImageView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialized();
	}

	protected void initialized() {
		super.setClickable(true);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setScaleType(ScaleType.MATRIX);
		setImageMatrix(imageUsingMatrix);
		setOnTouchListener(touchListener);
		mMapHandle = new MapHandleImp(this);
		mScaleDetector = new ScaleGestureDetector(getContext(),
				new ScaleListener());
		mRotateGestureDetector = new RotateGestureDetector(getContext(),
				new MyRotateGesture());
		mGestureDetector = new GestureDetector(getContext(),
				new MyGestureDetectorListener());
	}

	/**
	 * 放大固定倍数
	 * 
	 */
	public void pullScale() {
		if (!mMapHandle.isAniming() && mImageViewHelper.havePullScale) {
			mMapHandle.toScaleAnimator(getCurrentScale(),
					getCurrentScale() * 1.6f, new PointF(mView_width / 2,
							mView_height / 2));
		}
	}

	/**
	 * 放大固定倍数
	 * 
	 */
	public void pullScale(PointF mid) {
		if (!mMapHandle.isAniming() && mImageViewHelper.havePullScale) {
			float offsetX = mView_width / 2f - mid.x;
			float offsetY = mView_height / 2f - mid.y;
			if (offsetX >= -8 && offsetX <= 8 && offsetY >= -8 && offsetY <= 8) {
				postMatrixTranslate(offsetX, offsetY);
				postImageMatrix();
				mMapHandle.toScaleAnimator(getCurrentScale(),
						getCurrentScale() * 1.6f, mid);
				return;
			}
			AnimatorSet set = mMapHandle.getAnimatorSet();
			set.play(mMapHandle.getTranslateAnimAtor(offsetX, offsetY)).with(
					mMapHandle.getScaleAnimator(getCurrentScale(),
							getCurrentScale() * 1.6f, mid));
			set.start();
		}
	}

	/**
	 * 縮小
	 * 
	 */
	public void zoomScale() {
		if (!mMapHandle.isAniming() && mImageViewHelper.haveZoomScale) {
			mMapHandle.toScaleAnimator(getCurrentScale(),
					getCurrentScale() * 0.625f,
					mImageViewHelper.getCenterPoint(imageUsingMatrix));
		}
	}

	/**
	 * 旋轉回到原點
	 * 
	 */
	private void rotateToZero() {
		// if (saveRotate < 4 && saveRotate > -4) {
		// postMatrixRotate(saveRotate,
		// mImageViewHelper.getCenterPoint(imageUsingMatrix));
		// if (offsetX >= -4 && offsetX <= 4 && offsetY >= -4 && offsetY <= 4) {
		// postMatrixTranslate(offsetX, offsetY);
		// postImageMatrix();
		// } else {
		// translateOffset(offsetX, offsetY);
		// }
		// return;
		// }
		if (mMapHandle.isAniming()) {
			return;
		}
		// if (offsetX >= -4 && offsetX <= 4 && offsetY >= -4 && offsetY <= 4) {
		// postMatrixTranslate(offsetX, offsetY);
		// postImageMatrix();
		// mMapHandle.toRotateAnimator(saveRotate, center);
		// return;
		// }
		PointF center = mImageViewHelper.getCenterPoint(imageUsingMatrix);
		float offsetX = mView_width / 2f - center.x;
		float offsetY = mView_height / 2f - center.y;
		AnimatorSet animatorSet = mMapHandle.getAnimatorSet();
		animatorSet
				.play(mMapHandle.getTranslateAnimAtor(offsetX, offsetY))
				.with(mMapHandle.getScaleAnimator(getScale(),
						mBaZhanUtil.initScale, center))
				.with(mMapHandle.getRotateAnimator(saveRotate, (float) mBaZhanUtil.initAngle,  center));
		animatorSet.start();
	}
	
	/**
	 * 回归初始位置
	 * 
	 */
	public void releaseImageShow(){
		rotateToZero();
	}

	/**
	 * 移动偏移量
	 * 
	 * @param offsetX
	 * @param offsetY
	 */
	public void translateOffset(float offsetX, float offsetY) {
		mMapHandle.toTranslateAnimAtor(offsetX, offsetY);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeOnGlobalLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		mView_width = getWidth();
		mView_height = getHeight();
		mImageViewHelper.setViewSize(mView_width, mView_height);
		Drawable d = getDrawable(); // 获取当前显示的图片
		if (d == null)
			return;

		if (!isNewImageSVG)
			return;
		isNewImageSVG = false;
		saveRotate = 0;
		if (mOnRotateListener != null) {
			mOnRotateListener.onRotate(saveRotate);
		}
		int dw = d.getIntrinsicWidth();
		int dh = d.getIntrinsicHeight();

		mImageViewHelper.setDrawableSize(dw, dh);
		mImageViewHelper.initImageMatrix(imageUsingMatrix);
		
		//TODO: 巴展定制 --------------------------------------------------------------------
		
//		float angle = 90.1f;
		float scale = mView_height * 1f / dw;
		mBaZhanUtil = new BaZhanUtil(0, scale);
		PointF mid = new PointF(
                mView_width / 2f, mView_height / 2f);
        postMatrixScale(mBaZhanUtil.initScale / getScale(), mid);
        postMatrixRotate(mBaZhanUtil.initAngle, mid);

		saveRotate -= mBaZhanUtil.initAngle;
		mImageViewHelper.haveZoomScale = true;
		if (mImageViewHelper.mOnMinZoomCallback != null) {
			mImageViewHelper.mOnMinZoomCallback.onMinZoom(true);
		}
		
		//TODO: 巴展定制 --------------------------------------------------------------------
		
		postImageMatrix();
	}

	/**
	 * 设置是否允许单击移动到中心
	 * 
	 * @return
	 */
	public boolean isAllowTranslate() {
		return isAllowTranslate;
	}

	public void setAllowTranslate(boolean isAllowTranslate) {
		this.isAllowTranslate = isAllowTranslate;
	}

	public boolean isAllowRotate() {
		return isAllowRotate;
	}

	public void setAllowRotate(boolean isAllowRotate) {
		this.isAllowRotate = isAllowRotate;
		if (!isAllowRotate && saveRotate != 0) {
			rotateToZero();
		}
	}

	public float getScale() {
		return mImageViewHelper.getCurrentScale(imageUsingMatrix);
	}

	public ImageViewHelper getHelper() {
		return mImageViewHelper;
	}

	private GestureMode curGestureMode = GestureMode.none;

	private OnTouchListener touchListener = new OnTouchListener() {
		final static float MAX_VELOCITY = 1.2f;
		private int mLastPointerCount = -1;
		private long dragTime;
		private long lastDragTime;

		private PointF mDownPoint;
		private PointF mMovePoint;
        private boolean isTouch;
        long timeTag = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int pointerCount = event.getPointerCount();

			if (pointerCount > 2)
				return false;

			if (pointerCount != mLastPointerCount) {
				if (pointerCount == 1) {
					mDownPoint = new PointF(event.getX(), event.getY());
					curGestureMode = GestureMode.drag;
					lastDragTime = System.currentTimeMillis();
				} else {
					curGestureMode = GestureMode.rotate_scale;
				}
				mLastPointerCount = pointerCount;
			}

			if (curGestureMode == GestureMode.rotate_scale)
				midPoint(mid, event); // 獲取手指中心點

			mScaleDetector.onTouchEvent(event);
			mGestureDetector.onTouchEvent(event);

			if (isAllowRotate)
				mRotateGestureDetector.onTouchEvent(event);
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mDownPoint = new PointF(event.getX(), event.getY());
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				midPoint(mid, event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (curGestureMode == GestureMode.drag) {
					mMovePoint = new PointF(event.getX(), event.getY());
					if (Math.abs(mMovePoint.x - mDownPoint.x) > 8
							|| Math.abs(mMovePoint.y - mDownPoint.y) > 8) {
						dragTime = System.currentTimeMillis();
						timeTag = dragTime - lastDragTime;
						if(timeTag == 0)
						{
							timeTag = 100;
						}
						dragVelocity = new Float(distanceBetween(mMovePoint,
								mDownPoint)
								/ timeTag
								* FRICTION).floatValue();
						Log.e(">>>>",(dragTime - lastDragTime)+"");
						dragVelocity = Math.min(MAX_VELOCITY, dragVelocity);
						lastDragTime = dragTime;

						lastOffsetX = mMovePoint.x - mDownPoint.x;
						lastOffsetY = mMovePoint.y - mDownPoint.y;

						if(canChange) {
							postMatrixTranslate(lastOffsetX, lastOffsetY);
							mDownPoint.set(mMovePoint);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				if (curGestureMode == GestureMode.drag) {
					curGestureMode = GestureMode.toMove;
					inertiaMove();
				} else {
					curGestureMode = GestureMode.none;
				}
				mLastPointerCount = -1;
				break;
			}
			postImageMatrix();
			return true;
		}
	};

	public void setCanChange(boolean canChange) {
		this.canChange = canChange;
	}

	/**
	 * View被点击
	 * 
	 * @param xOnView
	 *            View上的X坐标
	 * @param yOnView
	 *            View上的Y坐标
	 */
	protected boolean onViewClick(float xOnView, float yOnView) {
		return false;
	}

	private float lastOffsetX;
	public float getLastOffsetX()
    {
        return lastOffsetX;
    }

    public void setLastOffsetX(float lastOffsetX)
    {
        this.lastOffsetX = lastOffsetX;
    }

    public float getLastOffsetY()
    {
        return lastOffsetY;
    }

    public void setLastOffsetY(float lastOffsetY)
    {
        this.lastOffsetY = lastOffsetY;
    }

    private float lastOffsetY;
	private float dragVelocity;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (curGestureMode == GestureMode.toMove) {
			inertiaMove();
		}
	}

	/**
	 * 获屏幕中心点相对于图片的对应点
	 * 
	 * @return
	 */
	public PointF getCenterByImage() {
		return mImageViewHelper.getSinglePoint(imageUsingMatrix, saveRotate,
				new PointF(mView_width / 2f, mView_height / 2f));
	}

	/**
	 * 惯性滑动
	 * 
	 */
	protected void inertiaMove() {
		final float deltaX = lastOffsetX * dragVelocity;
		final float deltaY = lastOffsetY * dragVelocity;

		Log.e(" inertiaMove ", deltaX + "," + deltaY);

		dragVelocity *= FRICTION;
		if (Math.abs(deltaX) < 0.01 && Math.abs(deltaY) < 0.01) {
			curGestureMode = GestureMode.none;
			return;
		}
		postMatrixTranslate(deltaX, deltaY);
		postImageMatrix();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (getDrawable() == drawable)
			return;
		imageUsingMatrix.reset();
		isNewImageSVG = true;
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		this.setImageDrawable(new MyBitmapDrawable(getResources(), bm));
	}

	private double distanceBetween(PointF left, PointF right) {
		return Math.sqrt(Math.pow(left.x - right.x, 2)
				+ Math.pow(left.y - right.y, 2));
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if(canChange) {
				float scaleFactor = detector.getScaleFactor();
				postMatrixScale(scaleFactor, mid);
			}
			return true;
		}
	}

	private float saveRotate = 0;
	public OnSingleClickListener mSingleClickListener;
	private OnRotateListener mOnRotateListener;
	private OnLongClickListener1 mOnLongClickListener;

	/**
	 * 旋转事件
	 * 
	 * @author libo
	 */
	private class MyRotateGesture extends SimpleOnRotateGestureListener {

		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			float toRotate = detector.getRotationDegreesDelta();
			if (toRotate > 360 || toRotate < -360) {
				toRotate %= 360;
			}
			if (toRotate == 0) {
				return false;
			}
			// float rotate = -toRotate;
			// saveRotate -= rotate;
			// if (saveRotate > 360 || saveRotate < -360) {
			// saveRotate %= 360;
			// }
			// if (mOnRotateListener != null) {
			// mOnRotateListener.onRotate(saveRotate);
			// }
			if(canChange) {
				postMatrixRotate(-toRotate, new PointF(mid.x, mid.y));
			}
			return true;
		}

	}

	private static class MyBitmapDrawable extends BitmapDrawable {
		public MyBitmapDrawable(Resources res, Bitmap bitmap) {
			super(res, bitmap);
		}
	};

	private enum GestureMode {
		toMove, doubleTap, drag, rotate_scale, none,
	}

	private class MyGestureDetectorListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mImageViewHelper.havePullScale) {
				float centerX = e.getX();
				float centerY = e.getY();

				if (mImageViewHelper.pointInArea(imageUsingMatrix, saveRotate,
						new PointF(centerX, centerY))) {
					mid.set(centerX, centerY);
					pullScale(mid);
				}
			}
			return true;
		}

		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			PointF cur = new PointF(e.getX(), e.getY());
			if (mImageViewHelper.pointInArea(imageUsingMatrix, saveRotate, cur)) {
				PointF point = mImageViewHelper.getSinglePoint(
						imageUsingMatrix, saveRotate, cur);
				if (mSingleClickListener != null) {
					mSingleClickListener.onSingle(point);
				}
				onViewClick(point.x, point.y);
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			PointF cur = new PointF(e.getX(), e.getY());
			if (mImageViewHelper.pointInArea(imageUsingMatrix, saveRotate, cur)) {
				PointF point = mImageViewHelper.getSinglePoint(
						imageUsingMatrix, saveRotate, cur);
				if (mOnLongClickListener != null) {
					mOnLongClickListener.onLongClick(point);
				}
			}
//			if (isAllowTranslate) {
//				float offsetX = mView_width / 2f - cur.x;
//				float offsetY = mView_height / 2f - cur.y;
//				translateOffset(offsetX, offsetY);
//			}
		}
	}

	/**
	 * 獲取矩阵當前縮放值
	 * 
	 * @return
	 */
	private float getCurrentScale() {
		imageUsingMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	/**
	 * 设置旋转
	 * 
	 * @param rotate
	 * @param pointF
	 */
	@Override
	public void postMatrixRotate(float rotate, PointF midPoint) {
		saveRotate -= rotate;
		if (saveRotate > 360 || saveRotate < -360) {
			saveRotate %= 360;
		}

		Log.e(" saveRotate ", saveRotate + "");

		if (mOnRotateListener != null) {
			mOnRotateListener.onRotate(saveRotate);
		}
		Log.e("rotate",rotate+"_"+midPoint.toString());
		imageUsingMatrix.postRotate(rotate, midPoint.x, midPoint.y);
	}

	/**
	 * 提交伸缩
	 * 
	 * @param scale
	 * @param centerX
	 * @param centerY
	 */
	@Override
	public void postMatrixScale(float scale, PointF midpPoint) {
		if (scale < 1 && !mImageViewHelper.haveZoomScale) {
			return;
		}
		if (scale > 1 && !mImageViewHelper.havePullScale) {
			return;
		}
		Log.e("scale",scale+"_"+midpPoint.toString());
		imageUsingMatrix.postScale(scale, scale, midpPoint.x, midpPoint.y);
	}

	/**
	 * 刷新矩阵显示
	 * 
	 */
	@Override
	public final void postImageMatrix() {
		checkScale();
		checkTranslate();
		setImageMatrix(imageUsingMatrix);
	}

	/**
	 * 提交平移变换
	 * 
	 * @param deltaX
	 *            平移距离X
	 * @param deltaY
	 *            平移距离Y
	 */
	@Override
	public void postMatrixTranslate(float deltaX, float deltaY) {
		if (deltaX == 0 && deltaY == 0) {
			return;
		}
		imageUsingMatrix.postTranslate(deltaX, deltaY);
	}

	@Override
	public void checkScale() {
		mImageViewHelper.checkScale(imageUsingMatrix, mid);
	}

	@Override
	public void checkTranslate() {
		mImageViewHelper.checkTranslate(imageUsingMatrix);
	}

	/**
	 * 设置单击返回点击点
	 * 
	 * @param listener
	 */
	public void setOnSingleClickListener(OnSingleClickListener listener) {
		this.mSingleClickListener = listener;
	}

	/**
	 * 设置旋转角度回调
	 * 
	 * @param listener
	 */
	public void setOnRotateListener(OnRotateListener listener) {
		this.mOnRotateListener = listener;
	}

	public void setOnLongClickListener(OnLongClickListener1 listener) {
		this.mOnLongClickListener = listener;
	}

	public interface OnLongClickListener1 {
		void onLongClick(PointF point);
	}

	public interface OnSingleClickListener {
		void onSingle(PointF p);
	}

	public interface OnRotateListener {
		void onRotate(float rotate);
	}
}