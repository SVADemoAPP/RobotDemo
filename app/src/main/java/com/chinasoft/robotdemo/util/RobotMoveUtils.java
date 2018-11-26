package com.chinasoft.robotdemo.util;

import com.slamtec.slamware.AbstractSlamwarePlatform;

/**
 * Created by XHF on 2018/11/23.
 * 机器人移动工具类
 */

public class RobotMoveUtils {
    private AbstractSlamwarePlatform mPlatform;                                     //机器人控制平台
    private static final int ROBOT_STOPING=0;                                       //机器人停止状态
    private static final int ROBOT_GO_FORWORD=1;                                    //机器人向前状态
    private static final int ROBOT_GO_BACK=-1;                                      //
    private static final int ROBOT_GO_LEFT=-2;
    private static final int ROBOT_GO_RIGHT=2;

}
