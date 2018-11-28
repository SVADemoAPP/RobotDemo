package com.chinasoft.robotdemo.robot;

import com.slamtec.slamware.robot.Location;

import java.util.Vector;

/**
 * Created by chinasoft_gyr on 2018/11/27.
 */

public interface OnRobotListener {

    void connectSuccess(float x, float y, float z, float direc);

    void connectFailed(String errormsg);

    void catchError(String errormsg);

    void refreshOrbits(Vector<Location> locVector);

    void notifyPrru(float x,float y);

    void positionChange(float x,float y,float direc);

    void moveFinish(float x,float y,float direc,boolean isForce);

}
