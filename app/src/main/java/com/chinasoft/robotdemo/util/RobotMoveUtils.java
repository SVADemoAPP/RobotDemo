package com.chinasoft.robotdemo.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;

import com.chinasoft.robotdemo.activity.HomeActivity;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Rotation;

/**
 * Created by XHF on 2018/11/23.
 * 机器人移动工具类
 */

public class RobotMoveUtils {
    private static AbstractSlamwarePlatform mPlatform;                                      //机器人控制平台
    public static final int ROBOT_STOPING = 0;                                       //机器人停止状态
    public static final int ROBOT_GO_FORWORD = 1;                                    //机器人向前状态
    public static final int ROBOT_GO_BACK = 2;                                      //机器人向后状态
    public static final int ROBOT_GO_LEFT = 3;                                      //机器人向左状态
    public static final int ROBOT_GO_RIGHT = 4;                                      //机器人向右状态
    public static final int ROBOT_GO_FORWORD_LEFT = 5;                               //机器人向左前方状态
    public static final int ROBOT_GO_FORWORD_RIGHT = 6;                              //机器人向右前方状态
    public static final int ROBOT_GO_BACK_LEFT = 7;                                  //机器人向左后方状态
    public static final int ROBOT_GO_BACK_RIGHT = 8;                                 //机器人向右后方状态
    private static int temp = -1;

    private static Handler handler = new Handler();

    private static Runnable task;

    /**
     * 摇杆坐标系和机器人坐标系转换方法
     */
    public static double cnTransformation(double angle) {
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
    public static int fuzzyDirection(double angle) {
        double rbRotation = cnTransformation(angle);
        if (rbRotation > -Math.PI / 8 && rbRotation < Math.PI / 8)                                                                          //判断控制机器人向上运动
        {
            Log.e("XHF", "判断控制机器人向上运动");
            return ROBOT_GO_FORWORD;
        } else if (rbRotation > Math.PI / 8 && rbRotation < Math.PI / 8 * 3) {                                                              //判断机器人向左上方45度角运动
            Log.e("XHF", "判断机器人向左上方45度角运动");
            return ROBOT_GO_FORWORD_LEFT;
        } else if (rbRotation > Math.PI / 8 * 3 && rbRotation < Math.PI / 8 * 5) {                                                          //判断机器人向左运动
            Log.e("XHF", "判断机器人向左运动");
            return ROBOT_GO_LEFT;
        } else if (rbRotation > Math.PI / 8 * 5 && rbRotation < Math.PI / 8 * 7) {                                                          //判断机器人向左下运动
            Log.e("XHF", "判断机器人向左下运动");
            return ROBOT_GO_BACK_LEFT;
        } else if ((rbRotation > Math.PI / 8 * 7 && rbRotation < Math.PI) || (rbRotation > -Math.PI && rbRotation < -Math.PI / 8 * 7)) {    //判断机器人向下运动
            Log.e("XHF", "判断机器人向下运动");
            return ROBOT_GO_BACK;
        } else if (rbRotation > -Math.PI / 8 * 7 && rbRotation < -Math.PI / 8 * 5) {                                                        //判断机器人向右下运动
            Log.e("XHF", "判断机器人向右下运动");
            return ROBOT_GO_BACK_RIGHT;
        } else if (rbRotation > -Math.PI / 8 * 5 && rbRotation < -Math.PI / 8 * 3) {                                                            //判断机器人向右移动
            Log.e("XHF", "判断机器人向右移动");
            return ROBOT_GO_RIGHT;
        } else if (rbRotation > -Math.PI / 8 * 3 && rbRotation < -Math.PI / 8) {                                                              //判断机器人向右上运动
            Log.e("XHF", "判断机器人向右上运动");
            return ROBOT_GO_FORWORD_RIGHT;
        }
        return -1;

    }

    /**
     * @param
     * @param direction
     */
    public static void setRobotMove(AbstractSlamwarePlatform platform, final int direction, final HomeActivity context) {
        mPlatform = platform;
        if (temp == direction) {
            return;
        }
        temp = direction;
        if (mPlatform != null) {
            try {
                handler.removeCallbacks(task);
                mPlatform.getCurrentAction().cancel();
                switch (direction) {
                    case ROBOT_GO_FORWORD:
                        mPlatform.rotateTo(new Rotation(0f)).waitUntilDone();
                        break;
                    case ROBOT_GO_BACK:
                        mPlatform.rotateTo(new Rotation((float) -Math.PI)).waitUntilDone();
                        break;
                    case ROBOT_GO_LEFT:
                        mPlatform.rotateTo(new Rotation((float) (Math.PI / 2))).waitUntilDone();
                        break;
                    case ROBOT_GO_RIGHT:
                        mPlatform.rotateTo(new Rotation((float) (-Math.PI / 2))).waitUntilDone();
                        break;
                    case ROBOT_GO_FORWORD_LEFT:
                        mPlatform.rotateTo(new Rotation((float) (Math.PI / 4))).waitUntilDone();
                        break;
                    case ROBOT_GO_FORWORD_RIGHT:
                        mPlatform.rotateTo(new Rotation((float) (-Math.PI / 4))).waitUntilDone();
                        break;
                    case ROBOT_GO_BACK_LEFT:
                        mPlatform.rotateTo(new Rotation((float) (3 * Math.PI / 4))).waitUntilDone();
                        break;
                    case ROBOT_GO_BACK_RIGHT:
                        mPlatform.rotateTo(new Rotation((float) (-3 * Math.PI / 4))).waitUntilDone();
                        break;
                }

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
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (OperationFailException e) {
                e.printStackTrace();
            }
            goStraight(direction);
        }

    }

    /***
     * 走直线
     */
    public static void goStraight(int direction) {
        try {
            if (direction == RobotMoveUtils.ROBOT_STOPING) {
                if (task != null) {
                    handler.removeCallbacks(task);
                }
                if (mPlatform != null) {
                    mPlatform.getCurrentAction().cancel();
                }
            } else {
                task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mPlatform != null) {
                                mPlatform.moveBy(MoveDirection.FORWARD);
                            }
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
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        } catch (OperationFailException e) {
                            e.printStackTrace();
                        }
                        handler.postDelayed(this, 500);
                    }
                };
                handler.post(task);
            }
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
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    //设置机器人旋转
    public static void setRobotRotation(AbstractSlamwarePlatform mPlatform, float angle) {
        if (mPlatform != null) {
            try {
                mPlatform.rotateTo(new Rotation(angle));
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
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (OperationFailException e) {
                e.printStackTrace();
            }
        }
    }

}
