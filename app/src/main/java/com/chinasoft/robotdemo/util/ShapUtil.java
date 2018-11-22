package com.chinasoft.robotdemo.util;

import android.content.Context;
import android.graphics.Path;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.PointShape;
import net.yoojia.imagemap.core.RoundShape;
import net.yoojia.imagemap.core.pRRUInfoShape;
import net.yoojia.imagemap.core.pRRUInfoShape.pRRUType;


public class ShapUtil {
    public static void addPointShap(String tag, int color, double x, double y, boolean animation, ImageMap1 map) {
        PointShape dy = new PointShape(tag, color);
        dy.setValues(String.format("%.5f:%.5f:8", new Object[]{Double.valueOf(x), Double.valueOf(y)}));
        map.addShape(dy, animation);
    }

    public static void addpRRUInfoShape(String tag, int color, Context context, double x, double y, boolean animation, ImageMap1 map) {
        pRRUInfoShape pRRUInfo = new pRRUInfoShape(tag, color, context);
        pRRUInfo.setPrruShowType(pRRUType.inArea);
        pRRUInfo.setValues(String.format("%.5f:%.5f:50", new Object[]{Double.valueOf(x), Double.valueOf(y)}));
        map.addShape(pRRUInfo, animation);
    }

    public static void addRoundShape(String tag, int color, double dis, float centerX, float centerY, boolean animation, ImageMap1 map) {
        RoundShape roundShape_stable = new RoundShape(tag, color);
        roundShape_stable.setValues(String.format("%.5f:%.5f:" + dis, new Object[]{Float.valueOf(centerX), Float.valueOf(centerY)}));
        map.addShape(roundShape_stable, animation);
    }

//    public static void addLineShape(String tag, int color, double newLocX, double newLocY, double oldLocX, double oldLocY, boolean isDrawDis, boolean animation, ImageMap1 map) {
//        LineShape lineShape = new LineShape(tag, color);
//        lineShape.setValues((float) newLocX, (float) newLocY, (float) oldLocX, (float) oldLocY);
//        Path path=new Path();
//        path.quadTo((float) oldLocX, (float) oldLocY, (float) newLocX, (float) newLocY);
//        lineShape.setPath(path);
////        lineShape.isDrawDis(isDrawDis);
//        map.addShape(lineShape, animation);
////    }
}
