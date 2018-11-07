package net.yoojia.imagemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * HighlightImageView基于TouchImageView的功能，在ImageView的Canvas上绘制一些形状。 Based on
 * TouchImageView class, Design for draw shapes on canvas of ImageView
 */
public class HighlightImageView extends TouchImageView implements
        ShapeExtension
{

    public HighlightImageView(Context context)
    {
        this(context, null);

    }

    public HighlightImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    private Map<Object, Shape> shapesCache = new HashMap<Object, Shape>(0);
    private OnShapeActionListener onShapeClickListener;

    public void setOnShapeClickListener(
            OnShapeActionListener onShapeClickListener)
    {
        this.onShapeClickListener = onShapeClickListener;
    }

    public void addShape(Shape shape, boolean isMove)
    {
        shapesCache.put(shape.tag, shape);
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
    }

    public List<Shape> getShapes()
    {
        return new ArrayList<Shape>(shapesCache.values());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (Shape shape : shapesCache.values())
        {
            shape.onDraw(canvas);
        }
        onDrawWithCanvas(canvas);
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
    protected void onViewClick(float xOnView, float yOnView)
    {
        if (onShapeClickListener == null)
        {
            return;
        }
        for (Shape shape : shapesCache.values())
        {
            if (shape.inArea(xOnView, yOnView))
            {
                // 如果一个形状被点击，通过监听接口回调给点击事件的关注者。
                // Callback by listener when a shape has been clicked
                
                // TODO：-------------------------
                
//                onShapeClickListener.onShapeClick(shape, xOnView, yOnView);
                break; // 只有一个形状可以被点击 - Only one shape can be click
            }
            onShapeClickListener.outShapeClick(xOnView, yOnView);
        }
    }

    @Override
    protected void postScale(float scaleFactor, float scaleCenterX,
            float scaleCenterY)
    {
        super.postScale(scaleFactor, scaleCenterX, scaleCenterY);
        if (scaleFactor != 0)
        {
            for (Shape shape : shapesCache.values())
            {
                if (scaleFactor != 0)
                {
                    shape.onScale(scaleFactor, scaleCenterX, scaleCenterY);
                }
            }
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY)
    {
        super.postTranslate(deltaX, deltaY);
        if (!(deltaX == 0 && deltaY == 0))
        {
            for (Shape shape : shapesCache.values())
            {
                shape.onTranslate(deltaX, deltaY);
            }
        }
    }

}
