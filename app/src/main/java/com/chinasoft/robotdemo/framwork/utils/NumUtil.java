package com.chinasoft.robotdemo.framwork.utils;


import java.text.DecimalFormat;

public class NumUtil {

    private static DecimalFormat df=new DecimalFormat("#.00");

    //float保留两位小数
    public static float saveTwoFloat(float f){
        return Float.parseFloat(df.format(f));
    }
}
