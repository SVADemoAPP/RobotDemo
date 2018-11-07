package net.yoojia.imagemap.core;

import net.yoojia.imagemap.support.ScaleUtility;
import net.yoojia.imagemap.util.MatrixConverHelper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;

public class LineShape extends Shape
{
    private float left;
    private float top;
    private float bottom;
    private float right;
    private Paint paint;
    private Path path;

    public LineShape(Object tag, int coverColor)
    {
        super(tag, coverColor);
        paint = new Paint();
        paint.setColor(Color.parseColor("#8bc34a"));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setAntiAlias(true);
        paint.setPathEffect(new CornerPathEffect(9.9f));
    }

    /**
     * set Left,Top,Right,Bottim
     * 
     * @param coords
     *            left,top,right,buttom
     */
    @Override
    public void setValues(float... coords)
    {
        if (coords == null || coords.length != 4)
        {
            throw new IllegalArgumentException(
                    "Please set values with 4 paramters: left,top,right,buttom");
        }
        left = coords[0];
        top = coords[1];
        right = coords[2];
        bottom = coords[3];
    }

    @Override
    public void onScale(float scale)
    {
        left *= scale;
        top *= scale;
        right *= scale;
        bottom *= scale;
    }
    public void setPath(Path path){
    	this.path = path;
    }

    @Override
    public void draw(Canvas canvas)
    {
    	/*PointF pa = MatrixConverHelper.mapMatrixPoint(mOverMatrix, left, top);
		PointF pb = MatrixConverHelper.mapMatrixPoint(mOverMatrix, right, bottom);*/
       // canvas.drawLine(pa.x, pa.y, pb.x, pb.y, paint);
        canvas.save();
        canvas.setMatrix(mOverMatrix);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    @Override
    public void scaleBy(float scale, float centerX, float centerY)
    {

        PointF leftTop = ScaleUtility.scaleByPoint(left, top, centerX, centerY,
                scale);
        left = leftTop.x;
        top = leftTop.y;

        PointF righBottom = ScaleUtility.scaleByPoint(right, bottom, centerX,
                centerY, scale);
        right = righBottom.x;
        bottom = righBottom.y;
    }

    @Override
    public void translate(float deltaX, float deltaY)
    {
        left += deltaX;
        right += deltaX;
        top += deltaY;
        bottom += deltaY;
    }

    @Override
    public boolean inArea(float x, float y)
    {
        boolean ret = false;
        if ((x > left) && (x < right))
        {
            if ((y > top) && (y < bottom))
            {
                ret = true;
            }
        }
        return false;
    }

    @Override
    public PointF getCenterPoint()
    {
        float centerX = (left + right) / 2.0f;
        float centerY = (top + bottom) / 2.0f;
        return new PointF(centerX, centerY);
    }

    @Override
    public String getUrl()
    {
        return null;
    }

    @Override
    public String getPictureUrl()
    {
        return null;
    }

    @Override
    public String getContent()
    {
        return null;
    }

    @Override
    public String getTitle()
    {
        return null;
    }

    @Override
    public boolean bubbleTag()
    {
        return false;
    }

	@Override
	public float getCenterX() {
		// TODO Auto-generated method stub
		return (left+right)/2;
	}

	@Override
	public float getCenterY() {
		// TODO Auto-generated method stub
		return (top+bottom)/2;
	}

}
