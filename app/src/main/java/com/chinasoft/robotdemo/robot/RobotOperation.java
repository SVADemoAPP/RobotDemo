package com.chinasoft.robotdemo.robot;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.sdp.CompositeMapHelper;
import com.slamtec.slamware.robot.SystemParameters;

import java.io.File;


/**
 * Created by chinasoft_gyr on 2018/11/27.
 */

public class RobotOperation {
    private OnRobotListener onRobotListener;
    private static AbstractSlamwarePlatform platform;
    private Location forwardLocation;
    private Pose nowPose;
    private float nowX, nowY, initZ, robotDirection;
    private Handler handler;
    private final String RUNNING = "RUNNING";
    private final String FINISHED = "FINISHED";
    private boolean isMoving;  //机器人action状态是否在移动
    private boolean isNotify =true; //是否不断进行通知
    private String currentMap;
    private Context context;
    private long updatePeriod; //更新间隔时间
    private boolean isAlwaysUpdate; //机器人是否始终定时更新位置（不管是否移动）
    private boolean isShowOrbits = true; //是否显示规划轨迹点
    private boolean mContinue = false;
    private String robotIp;
    private int robotPort;


    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(platform==null){
                Toast.makeText(context,"连接已断开！",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                nowPose = platform.getPose();
                nowX = nowPose.getX();
                nowY = nowPose.getY();
                if (isNotify) {
                    onRobotListener.notifyPrru(nowX, nowY);
                }
                if (isMoving) {
                    robotDirection = yawToDirec(nowPose.getYaw());
                    switch (platform.getCurrentAction().getStatus().toString()) {
                        case RUNNING:
                            onRobotListener.positionChange(nowX, nowY, robotDirection);
                            if (isShowOrbits) {
                                onRobotListener.refreshOrbits(platform.searchPath(forwardLocation).getPoints());
                            }
                            //如果机器人仅在移动时定时更新位置
                            if (!isAlwaysUpdate) {
                                handler.postDelayed(runnable, updatePeriod);
                            }
                            break;
                        case FINISHED:
                            isMoving = false;
                            onRobotListener.moveFinish(nowX, nowY, robotDirection, false);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                errorDisconnect(e.toString());
            }
            //如果机器人始终定时更新位置
            if (isAlwaysUpdate) {
                handler.postDelayed(runnable, updatePeriod);
            }
        }
    };

    /**
     * 测试连接
     *
     * @return
     */
    public static boolean testConnection(String robotIp, int robotPort) {
        try {
             platform = DeviceManager.connect(robotIp, robotPort); // 连接到机器人
            if (platform!=null)
            {
                return true;                                                               //不为空返回连接成功
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public RobotOperation(String robotIp, int robotPort, String currentMap, OnRobotListener onRobotListener, Context context, long updatePeriod) {
        this.context = context;
        this.currentMap = currentMap;
        this.onRobotListener = onRobotListener;
        this.updatePeriod = updatePeriod;
        this.robotIp=robotIp;
        this.robotPort=robotPort;
        handler = new Handler();
        forwardLocation = new Location();
        connect(robotIp, robotPort);
    }

    //机器人始终更新位置（不管是否移动）
    public void startOperation() {
        isAlwaysUpdate = true;
        handler.post(runnable);
    }

    //结束机器人更新位置
    public void endOperation() {
        isAlwaysUpdate = false;
        handler.removeCallbacks(runnable);
    }

    public void setShowOrbits(boolean showOrbits) {
        isShowOrbits = showOrbits;
    }

    //连接机器人
    private void connect(String robotIp, int robotPort) {
        try {
            platform = DeviceManager.connect(robotIp, robotPort); // 连接到机器人底盘
            if (platform==null)
            {
                onRobotListener.connectFailed("连接失败");
                return;
            }
            if(!TextUtils.isEmpty(Constant.robotSpeed)){
                platform.setSystemParameter(SystemParameters.SYSPARAM_ROBOT_SPEED,Constant.robotSpeed);
            }
//            platform.setSystemParameter(SystemParameters.SYSPARAM_ROBOT_SPEED,SystemParameters.SYSVAL_ROBOT_SPEED_HIGH);
//            platform.setSystemParameter(SystemParameters.SYSPARAM_ROBOT_SPEED,"0.6");
            nowPose = platform.getPose();// 当前机器人的位置,
            nowX = nowPose.getX();
            nowY = nowPose.getY();

            initZ = nowPose.getZ();
            forwardLocation.setZ(initZ);

            LLog.getLog().e("连接机器人成功", robotIp + ":" + robotPort);


            if ((nowX > 0.1f || nowY > 0.1f) && currentMap.equals(SharedPrefHelper.getString(context, "currentMap", ""))) {
                robotDirection = yawToDirec(nowPose.getYaw());
                onRobotListener.connectSuccess(nowX, nowY, robotDirection, true);
                mContinue = true;
            } else {
                mContinue = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LLog.getLog().e("连接机器人错误", e.toString());
            onRobotListener.connectFailed(e.toString());
        }
    }

    private boolean initCompositeMap(Pose pose) {
        String path = Constant.sdPath + "/stcms/"+currentMap.replace(".png",".stcm");
        if (new File(path).exists()) {
            CompositeMapHelper compositeMapHelper = new CompositeMapHelper();
            CompositeMap compositeMap = compositeMapHelper.loadFile(path);
            try {
                platform.setCompositeMap(compositeMap, pose);
//                LLog.getLog().robot("雷达地图", "载入成功");
                return true;
            } catch (Exception e) {
//                LLog.getLog().robot("雷达地图", "载入错误");
                e.printStackTrace();
                return false;
            }
        } else {
//            LLog.getLog().robot("雷达地图", "文件不存在");
            return false;
        }
    }

    //机器人yaw转换成角度
    private float yawToDirec(float yaw) {
        return -(float) (yaw * 180 / Math.PI);
    }

    //移动
    public void moveTo(float toX, float toY) {
        if(platform==null){
            Toast.makeText(context,"连接已断开！",Toast.LENGTH_SHORT).show();
            return;
        }
        isMoving = true;
        forwardLocation.setX(toX);
        forwardLocation.setY(toY);
        try {
//            onRobotListener.refreshOrbits(platform.searchPath(forwardLocation).getPoints());
            platform.moveTo(forwardLocation);
            if (!isAlwaysUpdate) {
                handler.post(runnable);
            }
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }

    }


    //取消动作并移动
    public void cancelAndMoveTo(float toX, float toY) {
        if (!isAlwaysUpdate) {
            handler.removeCallbacks(runnable);
        }
        cancelAction();
        moveTo(toX, toY);
    }


    //取消当前action
    public void cancelAction() {
        if(platform==null){
            Toast.makeText(context,"连接已断开！",Toast.LENGTH_SHORT).show();
            return;
        }
        isMoving = false;
        try {
            platform.getCurrentAction().cancel();
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }
    }

    public void forceStop() {
        if(platform==null){
            Toast.makeText(context,"连接已断开！",Toast.LENGTH_SHORT).show();
            return;
        }
        cancelAction();
        try {
            nowPose = platform.getPose();
            nowX = nowPose.getX();
            nowY = nowPose.getY();
            robotDirection = yawToDirec(nowPose.getYaw());
            onRobotListener.moveFinish(nowX, nowY, robotDirection, true);
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }

    }

    public void disconnect() {
        if (platform != null) {
            platform.disconnect();

            platform=null;
        }
    }

    private void errorDisconnect(String errormsg) {
        LLog.getLog().e("异常",errormsg);
//        disconnect();
        handler.removeCallbacks(runnable);
        disconnect();
        onRobotListener.catchError(errormsg);
    }


    /**
     * 在第一次进入点击坐标确认后执行
     */
    public void doAfterConfirm(float x, float y) {
        if(platform==null){
            Toast.makeText(context,"连接已断开！",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Pose pose = new Pose();
            pose.setX(x);
            pose.setY(y);
            pose.setZ(nowPose.getZ());
            pose.setYaw(0);
            if (initCompositeMap(pose)) {
                //停止地图更新，使用载入地图
                platform.setMapUpdate(false);
            }
            platform.setPose(pose);
            robotDirection = 0;
            onRobotListener.connectSuccess(x, y, robotDirection, false);
        } catch (RequestFailException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (ParseInvalidException e) {
            e.printStackTrace();
        }
    }

    public boolean getContinue() {
        return mContinue;
    }
}
