package com.chinasoft.robotdemo.util;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.chinasoft.robotdemo.entity.Floor;

import java.util.ArrayList;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class Constant {
    public static float firstX;
    public static float firstY;
    public static float mapScale;
    public static String robotIp;
    public static int robotPort;
    public static String sdPath;
    //机器人刷新间隔
    public static long updatePeriod;
    //路过画线间隔
    public static float lineSpace;
    public static Bitmap mapBitmap;
    public static String IP_ADDRESS ;
    public static InterRequestUtil interRequestUtil;
    public static RequestQueue mRequestQueue;
    public static String userId;
}
