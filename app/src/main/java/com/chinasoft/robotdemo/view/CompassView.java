package com.chinasoft.robotdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CompassView extends ImageView {
    private Drawable compass = null;
    private float mDirection = 0.0f;
    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas) {
        if (this.compass == null) {
            this.compass = getDrawable();
            this.compass.setBounds(0, 0, getWidth(), getHeight());
        }
        canvas.save();
        canvas.rotate(this.mDirection, (float) (getWidth() / 2), (float) (getHeight() / 2));
        this.compass.draw(canvas);
        canvas.restore();
        invalidate();
    }

    public void updateDirection(float direction) {
        this.mDirection = direction;
        invalidate();
    }
}
