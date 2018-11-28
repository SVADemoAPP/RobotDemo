package com.chinasoft.robotdemo.robot;
import android.os.Handler;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;


/**
 * Created by chinasoft_gyr on 2018/11/27.
 */

public class RobotOperation {
    private OnRobotListener onRobotListener;
    private AbstractSlamwarePlatform platform;
    private Location forwardLocation;
    private Pose nowPose;
    private float nowX, nowY, initZ, robotDirection;
    private Handler handler;
    private final String RUNNING="RUNNING";
    private final String FINISHED="FINISHED";
    private boolean isMoving,isShowBattery;

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                nowPose = platform.getPose();
                nowX = nowPose.getX();
                nowY = nowPose.getY();
                onRobotListener.notifyPrru(nowX, nowY);
                if(isMoving) {
                    robotDirection = yawToDirec(nowPose.getYaw());
                    switch (platform.getCurrentAction().getStatus().toString()) {
                        case RUNNING:
                            onRobotListener.positionChange(nowX,nowY,robotDirection);
                            onRobotListener.refreshOrbits(platform.searchPath(forwardLocation).getPoints());
                            break;
                        case FINISHED:
                            isMoving=false;
                            onRobotListener.moveFinish(nowX,nowY,robotDirection,false);
                            break;
                        default:
                            break;
                    }
                }
            }catch (Exception e){
                errorDisconnect(e.toString());
            }
            handler.postDelayed(runnable, Constant.updatePeriod);
        }
    };

    public RobotOperation(String robotIp, int robotPort, OnRobotListener onRobotListener) {
        this.onRobotListener = onRobotListener;
        handler=new Handler();
        forwardLocation = new Location();
        connect(robotIp,robotPort);
    }

    //开始
    public void startOperation(){
        handler.post(runnable);
    }

    //结束
    public void endOperation(){
        handler.removeCallbacks(runnable);
    }

    //连接机器人
    private void connect(String robotIp, int robotPort) {
        try {
            platform = DeviceManager.connect(robotIp, robotPort); // 连接到机器人底盘
            nowPose = platform.getPose();// 当前机器人的位置,
            nowX = nowPose.getX();
            nowY = nowPose.getY();
            initZ = nowPose.getZ();
            forwardLocation.setZ(initZ);
            robotDirection = yawToDirec(nowPose.getYaw());
            LLog.getLog().e("连接机器人成功", robotIp + ":" + robotPort);
            onRobotListener.connectSuccess(nowX, nowY, initZ, robotDirection);
        } catch (Exception e) {
            e.printStackTrace();
            LLog.getLog().e("连接机器人错误", e.toString());
            onRobotListener.connectFailed(e.toString());
        }
    }

    //机器人yaw转换成角度
    private float yawToDirec(float yaw) {
        return -(float) (yaw * 180 / Math.PI);
    }

    //移动
    public void moveTo(float toX, float toY) {
        isMoving=true;
        forwardLocation.setX(toX);
        forwardLocation.setY(toY);
        try {
            onRobotListener.refreshOrbits(platform.searchPath(forwardLocation).getPoints());
            platform.moveTo(forwardLocation);
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }

    }

    //取消动作并移动
    public void cancelAndMoveTo(float toX, float toY) {
        cancelAction();
        moveTo(toX,toY);
    }


    public void setShowBattery(boolean flag) {
        isShowBattery = flag;
    }


    //取消当前action
    public void cancelAction(){
        isMoving=false;
        try {
            platform.getCurrentAction().cancel();
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }
    }

    public void forceStop(){
        cancelAction();
        try {
            nowPose = platform.getPose();
            nowX = nowPose.getX();
            nowY = nowPose.getY();
            robotDirection = yawToDirec(nowPose.getYaw());
            onRobotListener.moveFinish(nowX,nowY,robotDirection,true);
        } catch (Exception e) {
            errorDisconnect(e.toString());
        }

    }

    public void disconnect(){
        if(platform!=null){
            platform.disconnect();
        }
    }

    private void errorDisconnect(String errormsg){
        disconnect();
        onRobotListener.catchError(errormsg);
    }
}
