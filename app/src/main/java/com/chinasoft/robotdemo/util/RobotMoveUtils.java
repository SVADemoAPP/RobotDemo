package com.chinasoft.robotdemo.util;

import android.util.Log;
import android.widget.Switch;

import com.slamtec.slamware.AbstractSlamwarePlatform;

/**
 * Created by XHF on 2018/11/23.
 * 机器人移动工具类
 */

public class RobotMoveUtils {
    private AbstractSlamwarePlatform mPlatform;                                     //机器人控制平台
    private static final int ROBOT_STOPING = 0;                                       //机器人停止状态
    private static final int ROBOT_GO_FORWORD = 1;                                    //机器人向前状态
    private static final int ROBOT_GO_BACK = -1;                                      //
    private static final int ROBOT_GO_LEFT = -2;
    private static final int ROBOT_GO_RIGHT = 2;

    /**
     * 摇杆坐标系和机器人坐标系转换方法
     */
    private double cnTransformation(double angle) {
        double rbRotationValue = 0f;
        double rbAngle = 270f - angle;//获得 机器人坐标系下，机器人度数
        if (rbAngle < 0) {
            rbAngle += 360f;
        }
        if (rbAngle >= 0 && rbAngle <= 180) {
            rbRotationValue = Math.toRadians(rbAngle);
        } else if (rbAngle > 180 && rbAngle < 360) {
            rbRotationValue = Math.toRadians(rbAngle) - 2 * Math.PI;
        }
        Log.e("XHF", "rbRotationValue=" + rbRotationValue);
        return rbRotationValue;
    }

    /**
     * 模糊判断机器人8个方向的运动
     *
     * @param angle
     */
    private void fuzzyDirection(double angle) {
        double rbRotation = cnTransformation(angle);
        if (rbRotation > -Math.PI / 8 && rbRotation < Math.PI / 8)                                                                          //判断控制机器人向上运动
        {
            Log.e("XHF","判断控制机器人向上运动");
        } else if (rbRotation > Math.PI / 8 && rbRotation < Math.PI / 8 * 3) {                                                              //判断机器人向左上方45度角运动
            Log.e("XHF","判断机器人向左上方45度角运动");
        } else if (rbRotation > Math.PI / 8 * 3 && rbRotation < Math.PI / 8 * 5) {                                                          //判断机器人向左运动
            Log.e("XHF","判断机器人向左运动");
        } else if (rbRotation > Math.PI / 8 * 5 && rbRotation < Math.PI / 8 * 7) {                                                          //判断机器人向左下运动
            Log.e("XHF","判断机器人向左下运动");
        } else if ((rbRotation > Math.PI / 8 * 7 && rbRotation < Math.PI) || (rbRotation > -Math.PI && rbRotation < -Math.PI / 8 * 7)) {    //判断机器人向下运动
            Log.e("XHF","判断机器人向下运动");
        } else if (rbRotation > -Math.PI / 8 * 7 && rbRotation < -Math.PI / 8 * 5) {                                                        //判断机器人向右下运动
            Log.e("XHF","判断机器人向右下运动");
        } else if (rbRotation > -Math.PI / 8 * 5 && rbRotation < -Math.PI / 8 * 3) {                                                            //判断机器人向右移动
            Log.e("XHF","判断机器人向右移动");
        } else if (rbRotation > -Math.PI / 8 * 3 && rbRotation < -Math.PI / 8) {                                                              //判断机器人向右上运动
            Log.e("XHF","判断机器人向右上运动");
        }

    }

}
