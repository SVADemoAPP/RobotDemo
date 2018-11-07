package net.yoojia.imagemap;

import net.yoojia.imagemap.core.Bubble;
import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.support.TranslateAnimation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * author : chenyoca@gmail.com date : 2013-5-19 An HTML map like widget in an
 * Android view controller
 */
public class ImageMap extends FrameLayout implements ShapeExtension,
        TranslateAnimation.OnAnimationListener
{

    private HighlightImageView highlightImageView;
    private Bubble bubble;
    private View viewForAnimation;
    private Context mContext;
    private View view;

    public ImageMap(Context context)
    {
        this(context, null);
        mContext = context;
    }

    public ImageMap(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialImageView(context);
    }

    public ImageMap(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialImageView(context);
    }

    private void initialImageView(Context context)
    {
        mContext = context;
        highlightImageView = new HighlightImageView(context);
//        highlightImageView.setOnShapeClickListener(this);
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
            Bubble.RenderDelegate renderDelegate)
    {
        if (bubbleView == null)
        {
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
    public void addShapeAndRefToBubble(final Shape shape, boolean isMove)
    {
        addShape(shape, isMove);
        if (bubble != null)
        {
            shape.createBubbleRelation(bubble);
        }
    }

    @Override
    public void onTranslate(float deltaX, float deltaY)
    {
        highlightImageView.moveBy(deltaX, deltaY);
    }

    
    @Override
    public void addShape(Shape shape, boolean isMove)
    {
        float scale = highlightImageView.getScale();
        shape.onScale(scale);
        // 将图像中心移动到目标形状的中心坐标上
        // Move the center point of the image to the target shape center.
        PointF from = highlightImageView.getAbsoluteCenter();
        PointF to = shape.getCenterPoint();
        TranslateAnimation movingAnimation = new TranslateAnimation(from.x,
                to.x, from.y, to.y);
        movingAnimation.setOnAnimationListener(this);
        movingAnimation.setInterpolator(new DecelerateInterpolator());
        movingAnimation.setDuration(500);
        movingAnimation.setFillAfter(true);
        if (isMove)
        {
            viewForAnimation.startAnimation(movingAnimation);
        }
        //
        PointF offset = highlightImageView.getAbsoluteOffset();
        shape.onTranslate(offset.x, offset.y);
        highlightImageView.addShape(shape, isMove);
    }

    public PointF getLocation()
    {

        PointF offset = highlightImageView.getAbsoluteOffset();
        return new PointF(offset.x, offset.y);

    }

    /**
     * 判断shape是否存在
     * 
     * @param tag
     * @return
     */
    public boolean getShape(Object tag)
    {
        boolean shape = highlightImageView.getShape(tag);
        return shape;
    }

    @Override
    public void removeShape(Object tag)
    {
        highlightImageView.removeShape(tag);
    }

    @Override
    public void clearShapes()
    {
        for (Shape item : highlightImageView.getShapes())
        {
            item.cleanBubbleRelation();
        }
        highlightImageView.clearShapes();
        if (bubble != null)
        {
            bubble.view.setVisibility(View.GONE);
        }
    }

//    @Override
//    public final void onShapeClick(Shape shape, float xOnImage, float yOnImage)
//    {
//        for (Shape item : highlightImageView.getShapes())
//        {
//            item.cleanBubbleRelation();
//        }
//        if (bubble != null)
//        {
//            bubble.showAtShape(shape);//點擊shape的時候bubble顯示在shaoe對應位置上
//            view.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * set a bitmap for image map.
     * 
     * @param bitmap
     *            image
     */
    public void setMapBitmap(Bitmap bitmap)
    {
        highlightImageView.setImageBitmap(bitmap);
    }
    
    public void setMapPicture(Picture picture){
        setMapDrawable(new PictureDrawable(picture));
    }
    
    
    public void setMapDrawable(Drawable d){
        highlightImageView.setImageDrawable(d);
    }

    public void reset()
    {
        removeAllViews();
        initialImageView(mContext);
    }

    /**
     * 获取bubble位置
     * 
     * @return
     */
    private PointF defaultPoint = new PointF(0.0f, 0.0f);

    public PointF getBubblePosition()
    {
        if (bubble != null)
        {
            return bubble.getBubblePosition();
        } else
        {
            return defaultPoint;
        }
    }

    public PointF getCenter()
    {
        float scale = highlightImageView.getScale();
        PointF center = highlightImageView.getAbsoluteCenter();
        return new PointF(center.x / scale, center.y / scale);
    }

    /**
     * 图片放大缩小倍数
     * 
     * @return
     */
    public float getZoom()
    {
        return highlightImageView.getScale();
    }

    public float getMinScale()
    {
        return highlightImageView.getMinScale();
    }

//    @Override
//    public void outShapeClick(float xOnImage, float yOnImage)
//    {
//        for (Shape item : highlightImageView.getShapes())
//        {
//            item.cleanBubbleRelation();
//        }
//        if (bubble != null)
//        {
//            view.setVisibility(View.INVISIBLE);
//        }
//    }

    public PointF getPoint()
    {
        float scale = highlightImageView.getScale();
        PointF center = highlightImageView.getPoints();
        return new PointF(center.x / scale, center.y / scale);
    }

    public void setBoo(boolean b)
    {
        highlightImageView.setBoolean(b);
    }

    public void setZoom(float scale, float x, float y)
    {
        highlightImageView.setZoom(scale, x, y);
    }

    public float getMesure()
    {
        return highlightImageView.getMesure();
    }

}
