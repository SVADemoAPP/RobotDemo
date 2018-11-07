package net.yoojia.imagemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.caverock.androidsvg.SVG;

import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.MoniPointShape;
import net.yoojia.imagemap.core.PushMessageShape;
import net.yoojia.imagemap.core.RequestShape;
import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.core.SpecialShape;
import net.yoojia.imagemap.util.MatrixConverHelper;
import net.yoojia.imagemap.util.SvgHelper;
import net.yoojia.imagemap.util.SvgHelper.SvgPath;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.MimeTypeMap;

/**
 * HighlightImageView基于TouchImageView的功能，在ImageView的Canvas上绘制一些形状。 Based on
 * TouchImageView class, Design for draw shapes on canvas of ImageView
 */
public class HighlightImageView1 extends TouchImageView1 implements
        ShapeExtension
{

    public boolean isOnArea;
    public int mFiterColor;

    public int getmFiterColor()
    {
        return mFiterColor;
    }

    public void setmFiterColor(int mFiterColor)
    {
        this.mFiterColor = mFiterColor;
    }

    public boolean isOnArea()
    {
        return isOnArea;
    }

    public void setOnArea(boolean isOnArea)
    {
        this.isOnArea = isOnArea;
    }

    public HighlightImageView1(Context context)
    {
        this(context, null);

    }

    public HighlightImageView1(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private Map<Object, Shape> shapesCache = new HashMap<Object, Shape>(0);
    private OnShapeActionListener onShapeClickListener;
    private SVG mSvgData;
    private List<SvgPath> mSvgPaths = new ArrayList<SvgPath>();
    private static final int SVGTOPATH_OK = 1;
    private Handler mhandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == SVGTOPATH_OK)
            {
                bindPathToShape(getShapes(), mSvgPaths);
            }
        }
    };

    private void bindPathToShape(final List<Shape> shapes,
            final List<SvgPath> svgPaths)
    {
        if (svgPaths.size() == 0 || shapes.size() == 0)
        {
            return;
        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                shape: for (int i = 0; i < shapes.size(); i++)
                {
                    int z = shapes.size();
                    Shape shape = shapes.get(i);
                    if (!(shape instanceof SpecialShape))
                    {
                        continue shape;
                    }
                    SpecialShape ss = (SpecialShape) shape;
                    if (ss.getSvgPath() != null)
                    {
                        continue shape;
                    }
                    for (int j = 0; j < svgPaths.size(); j++)
                    {
                        SvgPath p = svgPaths.get(j);
                        if (p.isArea(ss.getCenterX(), ss.getCenterY()))
                        {
                            ss.setSvgPath(p);
                            continue shape;
                        }
                    }
                }
            }
        }).start();
    };

    private void bindPathToShape(SpecialShape shape,
            final List<SvgPath> svgPaths)
    {
        for (int j = 0; j < svgPaths.size(); j++)
        {
            SvgPath p = svgPaths.get(j);
            // if (MatrixConverHelper.isArea(p.getPath(),
            // (int) shape.getCenterX(), (int) shape.getCenterY())
            // && p.getPaint().getColor() != Color.parseColor("#F8F8D6"))
            // {
            // p.setSelect(true);
            // shape.setAreaPath(p.getPath());
            // break;
            // }
            if (p.isArea(shape.getCenterX(), shape.getCenterY()))
            {
                shape.setSvgPath(p);
                break;
            }
        }
    }

    public boolean isFiter = false;
    public boolean isAllowRequestTranslate = false;

    public boolean isAllowRequestTranslate()
    {
        return isAllowRequestTranslate;
    }

    public void setAllowRequestTranslate(boolean isAllowRequestTranslate)
    {
        this.isAllowRequestTranslate = isAllowRequestTranslate;
    }

    public void setSvg(final SVG svg)
    {
        if (svg == null)
        {
            return;
        }
        clearShapes();
        mSvgData = svg;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SvgHelper svgHelper = new SvgHelper(new Paint());
                svgHelper.load(mSvgData);
                mSvgPaths = svgHelper.getPathsForViewport(
                        (int) svg.getDocumentWidth(),
                        (int) svg.getDocumentHeight());
                mhandler.sendEmptyMessage(SVGTOPATH_OK);
            }
        }).start();
    }

    public void setOnShapeClickListener(
            OnShapeActionListener onShapeClickListener)
    {
        this.onShapeClickListener = onShapeClickListener;
    }

    @Override
    public void addShape(Shape shape, boolean isMove)
    {
        shapesCache.put(shape.tag, shape);
        if (shape instanceof SpecialShape)
        {
            bindPathToShape((SpecialShape) shape, mSvgPaths);
        }

        if (isMove)
        {
            PointF point = new PointF(shape.getCenterX(), shape.getCenterY());
            requestTranslate(point);
//            return;
        }

        postInvalidate();
    }

    @Override
    public void removeShape(Object tag)
    {
        if (shapesCache.containsKey(tag))
        {
            shapesCache.remove(tag);
            postInvalidate();
        }
    }

    /**
     * 判断shape是否存在
     * 
     * @param tag
     * @return
     */
    public boolean getShape(Object tag)
    {
        if (shapesCache.containsKey(tag))
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void clearShapes()
    {
        shapesCache.clear();
        mSvgPaths.clear();
    }

    public List<Shape> getShapes()
    {
        return new ArrayList<Shape>(shapesCache.values());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Matrix matrix = new Matrix(getImageMatrix());
        super.onDraw(canvas);
        float scale = getScale();
        for (Shape shape : shapesCache.values())
        {
            shape.postMatrixCahnge(matrix);
            shape.setScale(scale);
            shape.onDraw(canvas);
        }
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setStyle(Style.STROKE);
//        paint.setStrokeWidth(6);
//        paint.setColor(Color.parseColor("#8812ee"));
//        for (int i = 0; i < mSvgPaths.size(); i++)
//        {
//            SvgPath mSvgPath = mSvgPaths.get(i);
//            if (mSvgPath != null)
//            {
//                if (mSvgPath.isSelect())
//                {
//                    paint.setColor(Color.parseColor("#88ff44"));
//                } else
//                {
//                    paint.setColor(Color.parseColor("#8812ee"));
//                }
//                canvas.save();
//                canvas.setMatrix(matrix);
//                switch (mSvgPath.getType())
//                {
//                case path:
//                    canvas.drawPath(mSvgPath.getPath(), paint);
//                    break;
//                case rect:
//                    canvas.drawRect(mSvgPath.getRect(), paint);
//                    break;
//                }
//                canvas.restore();
//            }
//
//        }

        onDrawWithCanvas(canvas);
    }

    private void requestTranslate(PointF point)
    {
        if (!mMapHandle.isAniming())
        {
            point = MatrixConverHelper.mapMatrixPoint(getImageMatrix(),
                    point.x, point.y);
            float dx = mView_width / 2f - point.x;
            float dy = mView_height / 2f - point.y;

            if (Math.abs(dx) < 4 && Math.abs(dy) < 4)
            {
                return;
            }

            translateOffset(dx, dy);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
    }

    /**
     * 如果继承HighlightImageView，并需要在Canvas上绘制，可以Override这个方法来实现。 - Override this
     * method for draw something on canvas when YourClass extends
     * HighlightImageView.
     * 
     * @param canvas
     *            画布
     */
    protected void onDrawWithCanvas(Canvas canvas)
    {
    }

    @Override
    protected boolean onViewClick(float xOnView, float yOnView)
    {
        for (Shape shape : shapesCache.values())
        {
            if (shape.inArea(xOnView, yOnView))
            {
                if (shape instanceof SpecialShape)
                {
                    if (onShapeClickListener != null)
                    {
                        onShapeClickListener.onSpecialShapeClick(
                                (SpecialShape) shape, shape.getCenterX(),
                                shape.getCenterY());
                    }
                } else if (shape instanceof PushMessageShape)
                {
                    if (onShapeClickListener != null)
                    {
                        onShapeClickListener.onPushMessageShapeClick(
                                (PushMessageShape) shape, shape.getCenterX(),
                                shape.getCenterY());
                    }
                }
                else if(shape instanceof CollectPointShape){
                	if (onShapeClickListener != null)
                    {
                        onShapeClickListener.onCollectShapeClick(
                                (CollectPointShape) shape, shape.getCenterX(),
                                shape.getCenterY());
                    }
                }
                else if(shape instanceof MoniPointShape){
                	if (onShapeClickListener != null)
                    {
                        onShapeClickListener.onMoniShapeClick(
                                (MoniPointShape) shape, shape.getCenterX(),
                                shape.getCenterY());
                    }
                }
                PointF point = new PointF(shape.getCenterX(),
                        shape.getCenterY());
                point = MatrixConverHelper.mapMatrixPoint(getImageMatrix(),
                        point.x, point.y);
                if (isAllowTranslate)
                {
                    float offsetX = mView_width / 2f - point.x;
                    float offsetY = mView_height / 2f - point.y;
                    translateOffset(offsetX, offsetY);
                }
                return true;
            }
        }
        if (onShapeClickListener != null)
        {
            onShapeClickListener.outShapeClick(xOnView, yOnView);
        }
        return false;
    }
}
