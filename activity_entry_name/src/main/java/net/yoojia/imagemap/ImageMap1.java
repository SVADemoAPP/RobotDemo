package net.yoojia.imagemap;

import net.yoojia.imagemap.TouchImageView1.OnLongClickListener1;
import net.yoojia.imagemap.TouchImageView1.OnRotateListener;
import net.yoojia.imagemap.TouchImageView1.OnSingleClickListener;
import net.yoojia.imagemap.core.Bubble;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.MoniPointShape;
import net.yoojia.imagemap.core.PushMessageShape;
import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.core.SpecialShape;
import net.yoojia.imagemap.support.TranslateAnimation;
import net.yoojia.imagemap.util.ImageViewHelper;
import net.yoojia.imagemap.util.ImageViewHelper.OnMaxZoomCallback;
import net.yoojia.imagemap.util.ImageViewHelper.OnMinZoomCallback;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.caverock.androidsvg.SVG;

/**
 * author : chenyoca@gmail.com date : 2013-5-19 An HTML map like widget in an
 * Android view controller
 */
public class ImageMap1 extends FrameLayout implements ShapeExtension,
		ShapeExtension.OnShapeActionListener,
		TranslateAnimation.OnAnimationListener {

	private HighlightImageView1 highlightImageView;
	private Bubble bubble;
	private View viewForAnimation;
	private Context mContext;
	private View view;

	public ImageMap1(Context context) {
		this(context, null);
		mContext = context;
	}

	public ImageMap1(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialImageView(context);
	}

	public ImageMap1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialImageView(context); // 這個我真不知道怎麼搞的了那我自己看看吧
	}

	private void initialImageView(Context context) {
		mContext = context;
		highlightImageView = new HighlightImageView1(context);
		highlightImageView.setOnShapeClickListener(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(highlightImageView, params);
		viewForAnimation = new View(context);
		addView(viewForAnimation, 0, 0);
	}

	/**
	 * Set a bubble view controller and it's renderDelegate interface.
	 * 
	 * @param bubbleView
	 *            A view controller object for display on image map.
	 * @param renderDelegate
	 *            The display interface for bubble view controller render.
	 */
	public void setBubbleView(View bubbleView,
			Bubble.RenderDelegate renderDelegate) {
		if (bubbleView == null) {
			throw new IllegalArgumentException(
					"View for bubble cannot be null !");
		}
		view = bubbleView;
		bubble = new Bubble(view);
		bubble.setRenderDelegate(renderDelegate);
		addView(bubble);
		bubbleView.setVisibility(View.GONE);
	}

	// setAllBunbleView
	/**
	 * 添加Shape，并关联到Bubble的位置 - Add a shape and set reference to the bubble.
	 * 
	 * @param shape
	 *            Shape
	 */
	public void addShapeAndRefToBubble(final Shape shape, boolean isMove) {
		addShape(shape, isMove);
		if (bubble != null) {
			shape.createBubbleRelation(bubble);
		}
	}

	@Override
	public void onTranslate(float deltaX, float deltaY) {
		highlightImageView.postMatrixTranslate(deltaX, deltaY);
		highlightImageView.postImageMatrix();
	}

	@Override
	public void addShape(Shape shape, boolean isMove) {
		highlightImageView.addShape(shape, isMove);
	}

	/**
	 * 判断shape是否存在
	 * 
	 * @param tag
	 * @return
	 */
	public boolean getShape(Object tag) {
		boolean shape = highlightImageView.getShape(tag);
		return shape;
	}

	@Override
	public void removeShape(Object tag) {
		highlightImageView.removeShape(tag);
	}

	@Override
	public void clearShapes() {
		for (Shape item : highlightImageView.getShapes()) {
			item.cleanBubbleRelation();
		}
		highlightImageView.clearShapes();
		if (bubble != null) {
			bubble.view.setVisibility(View.GONE);
		}
	}

	/**
	 * set a bitmap for image map.
	 * 
	 * @param bitmap
	 *            image // 沒喇叭 BUbble是什麼啊 就是點擊shape，shape上彈出的那個水滴
	 */
	public void setMapBitmap(Bitmap bitmap) {
		highlightImageView.setImageBitmap(bitmap);
	}

	public void setMapPicture(Picture picture) {
		setMapDrawable(new PictureDrawable(picture));
	}

	public void setMapDrawable(Drawable d) {
		highlightImageView.setImageDrawable(d);
	}

	public float getLastOffsetX() {
		return highlightImageView.getLastOffsetX();
	}

	public float getLastOffsetY() {
		return highlightImageView.getLastOffsetY();
	}

	public void reset() {
		removeAllViews();
		initialImageView(mContext);
	}

	/**
	 * 获取bubble位置
	 * 
	 * @return
	 */
	private PointF defaultPoint = new PointF(0.0f, 0.0f);
	private OnShapeActionListener mOnShapeClickListener;

	public PointF getBubblePosition() {
		if (bubble != null) {
			return bubble.getBubblePosition();
		} else {
			return defaultPoint;
		}
	}

	/**
	 * 图片放大缩小倍数
	 * 
	 * @return
	 */
	public float getZoom() {
		return highlightImageView.getScale();
	}

	public float getMinScale() {
		return highlightImageView.getHelper().getMinZoomScale();
	}

	public boolean isOnArea() {
		return highlightImageView.isOnArea();
	}

	@Override
	public void outShapeClick(float xOnImage, float yOnImage) {
		if (mOnShapeClickListener != null) {
			mOnShapeClickListener.outShapeClick(xOnImage, yOnImage);
			for (Shape item : highlightImageView.getShapes()) {
				item.cleanBubbleRelation();
			}
			if (bubble != null) {
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置区域svg 用于区域选择
	 * 
	 * @param svg
	 */
	public void setSvg(SVG svg) {
		highlightImageView.setSvg(svg);
	}

	/**
	 * 單擊放大
	 * 
	 */
	public void pullScale() {
		highlightImageView.pullScale();
	}

	/**
	 * 單擊縮小
	 * 
	 */
	public void zoomScale() {
		highlightImageView.zoomScale();
	}

	public ImageViewHelper getHelper() {
		return highlightImageView.getHelper();
	}

	/**
	 * 设置最大监听
	 * 
	 * @param callback
	 */
	public void setMaxCallback(OnMaxZoomCallback callback) {
		getHelper().setOnMaxZoomScaleCallback(callback);
	}

	/**
	 * 设置最小监听
	 * 
	 * @param callback
	 */
	public void setMinCallback(OnMinZoomCallback callback) {
		getHelper().setOnMinZoomScaleCallback(callback);
	}

	/**
	 * 设置角度监听...
	 * 
	 * @param listener
	 */
	public void setOnRotateListener(OnRotateListener listener) {
		highlightImageView.setOnRotateListener(listener);
	}

	/**
	 * 设置取消旋转滑动后的值回调
	 * @param listener
	 */
	public void setOnCenerPointListener(TouchImageView1.OnCenterPointListener listener) {
		highlightImageView.setCenterPointListener(listener);
	}
	/**
	 * 设置单击监听
	 * 
	 * @param listener
	 */
	public void setOnSingleClickListener(OnSingleClickListener listener) {
		highlightImageView.setOnSingleClickListener(listener);
	}

	public void setOnLongClickListener1(OnLongClickListener1 listener) {
		highlightImageView.setOnLongClickListener(listener);
	}

	/**
	 * 設置是否允許旋轉
	 * 
	 * @param allow
	 */
	public void setAllowRotate(boolean allow) {
		highlightImageView.setAllowRotate(allow);
	}

	public void setCanChange(boolean canChange){
		highlightImageView.setCanChange(canChange);
	}

	public void releaseImageShow() {
		highlightImageView.releaseImageShow();
	}

	public PointF getCenterByImagePoint() {
		return highlightImageView.getCenterByImage();
	}

	public void setAllowRequestTranslate(boolean isAllow) {
		highlightImageView.setAllowRequestTranslate(isAllow);
	}

	/**
	 * 獲取當前是否允許旋轉的狀態
	 * 
	 * @return
	 */
	public boolean isAllowRotate() {
		return highlightImageView.isAllowRotate();
	}

	/**
	 * 设置是否允许单击移动到中心
	 * 
	 * @return
	 */
	public boolean isAllowTranslate() {
		return highlightImageView.isAllowTranslate();
	}

	public void setAllowTranslate(boolean isAllowTranslate) {
		highlightImageView.setAllowTranslate(isAllowTranslate);
	}

	public void setFiterColor(int color) {
		highlightImageView.setmFiterColor(color);
	}

	public void setFiter(boolean isFiter) {
		highlightImageView.isFiter = isFiter;
	}

	public void setOnShapeClickListener(OnShapeActionListener listener) {
		this.mOnShapeClickListener = listener;
	}

	@Override
	public void onSpecialShapeClick(SpecialShape shape, float xOnImage,
			float yOnImage) {
		if (mOnShapeClickListener != null) {
			mOnShapeClickListener
					.onSpecialShapeClick(shape, xOnImage, yOnImage);
			for (Shape item : highlightImageView.getShapes()) {
				item.cleanBubbleRelation();
			}
			if (bubble != null) {
				bubble.showAtShape(shape);
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onPushMessageShapeClick(PushMessageShape shape, float xOnImage,
			float yOnImage) {
		if (mOnShapeClickListener != null) {
			mOnShapeClickListener.onPushMessageShapeClick(shape, xOnImage,
					yOnImage);
			for (Shape item : highlightImageView.getShapes()) {
				item.cleanBubbleRelation();
			}
			if (bubble != null) {
				bubble.showAtShape(shape);
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onCollectShapeClick(CollectPointShape shape, float xOnImage,
			float yOnImage) {

		if (mOnShapeClickListener != null) {
			mOnShapeClickListener
					.onCollectShapeClick(shape, xOnImage, yOnImage);
			for (Shape item : highlightImageView.getShapes()) {
				item.cleanBubbleRelation();
			}
			if (bubble != null) {
				bubble.showAtShape(shape);
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onMoniShapeClick(MoniPointShape shape, float xOnImage,
			float yOnImage) {
		// TODO Auto-generated method stub
		if (mOnShapeClickListener != null) {
			mOnShapeClickListener
					.onMoniShapeClick(shape, xOnImage, yOnImage);
			for (Shape item : highlightImageView.getShapes()) {
				item.cleanBubbleRelation();
			}
			if (bubble != null) {
				bubble.showAtShape(shape);
				view.setVisibility(View.VISIBLE);
			}
		}
	}
}
