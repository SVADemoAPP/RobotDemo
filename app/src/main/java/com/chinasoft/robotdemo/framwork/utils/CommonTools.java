package com.chinasoft.robotdemo.framwork.utils;

import android.content.Context;

import java.util.regex.Pattern;
public class CommonTools {

    public static int dp2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        return ((int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f)) - 15;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            return context.getResources().getDimensionPixelSize(Integer.parseInt(c.getField("status_bar_height").get(c.newInstance()).toString()));
        } catch (Exception e) {
            return statusBarHeight;
        }
    }

    public static boolean isMobileNO(String mobiles) {
        return Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17([0,1,6,7,]))|(18[0-2,5-9]))\\d{8}$").matcher(mobiles).matches();
    }

}
