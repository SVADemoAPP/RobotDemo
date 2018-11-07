package com.chinasoft.robotdemo.framwork.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
public class StringUtil {
    /**
     * 判断字符串是否为null或者空字符串
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        boolean result = false;
        if (null == str || "".equals(str.trim())|| "null".equals(str.trim())) {
            result = true;
        }
        return result;
    }

    /**
     * 如果小于两位数，添加0后生成string
     * @param
     * @return
     */
    public static String addZreoIfLessThanTen(long ballNum) {

        String string = "";
        if (ballNum < 10) {
            string = "0" + ballNum;
        } else {
            string = String.valueOf(ballNum);
        }
        return string;
    }

    /**
     *
     * @param string
     * @return
     */
    public static boolean isNotNull(String string) {
        if (null != string && !"".equals(string.trim())&&!"null".equals(string.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 去掉一个字符串中的所有的单个空格" "
     * @param string
     */
    public static String replaceSpaceCharacter(String string) {
        if (null == string || "".equals(string)) {
            return "";
        }
        return string.replace(" ", "");
    }

    /**
     * 获取小数位为6位的经纬度
     * @param string
     * @return
     */
    public static String getStringLongitudeOrLatitude(String string) {
        if (StringUtil.isNullOrEmpty(string)) {
            return "";
        }
        if (string.contains(".")) {
            String[] splitArray = string.split("\\.");
            if (splitArray[1].length() > 6) {
                String substring = splitArray[1].substring(0, 6);
                return splitArray[0] + "." + substring;
            } else {
                return string;
            }
        } else {
            return string;
        }
    }

    private static BigDecimal bigDecimal;
    private static DecimalFormat fnum;
    /**
     * 保留小数点后两位
     * @param value
     * @return
     */
    public static String getStringFormat(double value){
        if(value==0){
            return "0";
        }
        try {
            bigDecimal   =   new   BigDecimal(value);
            fnum = new DecimalFormat("#0.00");
            return fnum.format(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
    /**
     * 保留小数点后一位
     * @param value
     * @return
     */
    public static String getOneStringFormat(double value){
        if(value==0){
            return "0";
        }
        try {
            bigDecimal   =   new   BigDecimal(value);
            fnum = new DecimalFormat("#0.0");
            return fnum.format(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
    /**
     * 保留小数点后一位
     * @param value
     * @return
     */
    public static String getTwoStringFormat(double value){
        if(value==0){
            return "0";
        }
        try {
            bigDecimal   =   new   BigDecimal(value);
            fnum = new DecimalFormat("#0.00");
            return fnum.format(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}

