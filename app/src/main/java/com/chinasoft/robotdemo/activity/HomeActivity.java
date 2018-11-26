package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.PrruModelListAdapter;
import com.chinasoft.robotdemo.bean.LocAndPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.bean.PrruSigalModel;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.util.SuperPopupWindow;
import com.chinasoft.robotdemo.view.CompassView;
import com.chinasoft.robotdemo.view.dialog.ParamsDialog;
import com.chinasoft.robotdemo.view.dialog.RobotparamDialog;
import com.google.gson.Gson;
import com.kongqw.rockerlibrary.view.RockerView;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.PowerStatus;
import com.slamtec.slamware.robot.Rotation;
import com.slamtec.slamware.robot.SleepMode;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.RequestShape;
import net.yoojia.imagemap.core.RobotShape;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class HomeActivity extends BaseActivity {


    private ImageMap1 map;

    private ParamsDialog paramsDialog;

    private RobotparamDialog robotparamDialog;

    private AbstractSlamwarePlatform platform;

    private Pose nowPose;

    private int mapWidth, mapHeight;

    private boolean robotConnect = false;

    private boolean isStart = false;

    private boolean isPrruCollect = false;

    private PrruModel nowCollectPrru;

    private float xo, yo, scale, initX, initY, initZ, realXo, realYo;

//    private RobotShape robotShape;

    private Location forwardLocation;

    private Vector<Location> locVector;

//    private List<CoorInMap> coorOrbit=new ArrayList<>();

    private int coorCount = 0;

    private IAction nowAction;

    private LineShape line;

    private Path path;

    private LineShape lineShape;

    private float lastX, lastY, nowX, nowY;

    private float[] newF;

    private Bitmap mapBitmap;

    private boolean isContinue;

    private String currentMap;

    private TextView tv_status, tv_setting, tv_connect;

    private ImageView iv_operation;

    private RequestShape robotShape;

    private CompassView cv;

//    private boolean flag = false;

//    private List<PrruModel> prruModelList = new ArrayList<>();

    private int cStep;

    private String nowCollectNeCode;

    private float maxRsrp, xWhenMax, yWhenMax;

    private float[] xyRobotWhenMax;

    private Timer timer = new Timer();

    private CustomShape desShape;

    private float[] desXY;

    /***xhf***/
    private SuperPopupWindow mSuperPopupWindow;

    private Context mContext;
    private View popupView;

    private SuperPopupWindow mConnectPopupWindow;

    private float mapRotate;

    private float robotDirection;

    private boolean mSwitchAutoFlag = true; //默认为自动
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {

            if (platform == null) {
                LLog.getLog().robot("no connect", "未连接");
//                getRobotInfo();
            } else {
                try {
                    LLog.getLog().robot("health", "" + platform.getRobotHealth().getErrors());
                } catch (Exception e) {
                    LLog.getLog().robot("error", e.toString());
                }

            }
        }
    };

    private int moveCode;   //


    private Handler mMoveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (platform != null) {
                try {
                    switch (msg.what) {
                        case 101:
                            platform.moveBy(MoveDirection.FORWARD);
//                        platform.rotateTo(new Rotation((float) Math.PI/4));
                            break;
                        case 102:
                            platform.moveBy(MoveDirection.BACKWARD);
                            break;
                        case 103:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        moveCode = 100;
                                        IMoveAction iMoveAction = platform.moveBy(MoveDirection.TURN_LEFT);
                                        iMoveAction.waitUntilDone();
                                        moveCode = 101;
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
                            }).start();
//                            getRobotInfo();
                            break;
                        case 104:
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        moveCode = 100;
                                        IMoveAction iMoveAction = platform.moveBy(MoveDirection.TURN_RIGHT);
                                        iMoveAction.waitUntilDone();
                                        moveCode = 101;
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
                            }).start();
                            getRobotInfo();
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
            }
        }
    };
    Timer mMoveTimer;


    /***xhf***/
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        nowPose = platform.getPose();
                        robotDirection = -yawToRotation(nowPose.getYaw());
                        cv.updateDirection(mapRotate + robotDirection);
                        Log.e("dir2", "dd:" + robotDirection);
                        robotShape.setView(cv);
                        map.addShape(robotShape, false);
//                        Log.e("msg", "x:" + nowPose.getX() + ",y:" + nowPose.getY());
                        nowX = nowPose.getX();
                        nowY = nowPose.getY();
                        final float logX = nowX - initX + realXo;
                        final float logY = nowY - initY + realYo;
                        Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + Constant.userId + "&mapId=1", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                LLog.getLog().e("getLocAndPrruInfo成功", s);
                                LocAndPrruInfoResponse lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                                if (lap.code == 0) {
                                    LLog.getLog().prru(logX + "," + logY, prruDataToString(lap.data.prruData));
                                    if (isPrruCollect) {
                                        Float rsrp = getRsrpByGpp(nowCollectNeCode, lap.data.prruData);
                                        recordMaxRsrp(rsrp, logX, logY);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                LLog.getLog().e("getLocAndPrruInfo错误", volleyError.toString());
                            }
                        });
//                        Log.e("handler",(nowX - lastX)+","+(nowY - lastY)+" now:"+nowX+","+nowY+" last:"+lastX+","+lastY);
                        if (Math.sqrt((nowX - lastX) * (nowX - lastX) + (nowY - lastY) * (nowY - lastY)) > 0.3) {
//                            Log.e("handler","的点点滴滴顶顶顶顶顶顶顶顶顶大等等");
                            newF = realToMap(nowX, nowY);
                            path.lineTo(newF[0], newF[1]);
                            lineShape.setPath(path);
                            map.addShape(lineShape, false);
                            lastX = nowX;
                            lastY = nowY;
                        }
                        Log.e("action", "action:" + platform.getCurrentAction().getActionName());
//                        showToast("action:" + platform.getCurrentAction().getActionName());
                        float[] p = realToMap(nowPose.getX(), nowPose.getY());
                        for (int i = 0, len = coorCount; i < len; i++) {
                            map.removeShape("coor" + i);
                        }
                        robotShape.setValues(String.format(
                                "%.5f:%.5f",
                                new Object[]{p[0],
                                        p[1]}));
                        map.addShape(robotShape, false);
                        switch (platform.getCurrentAction().getActionName()) {
                            case "":
                                removeMessages(0);
                                newF = realToMap(nowX, nowY);
                                path.lineTo(newF[0], newF[1]);
                                lineShape.setPath(path);
                                map.addShape(lineShape, false);
                                map.removeShape("des");
                                lastX = nowX;
                                lastY = nowY;
                                if (isPrruCollect) {
                                    cStep++;
                                    switch (cStep) {
                                        case 1:
                                            LLog.getLog().e("扫描", "1");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX + 1, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 2:
                                            LLog.getLog().e("扫描", "2");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 3:
                                            LLog.getLog().e("扫描", "3");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY + 1);
                                            break;
                                        case 4:
                                            LLog.getLog().e("扫描", "4");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 5:
                                            LLog.getLog().e("扫描", "5");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX - 1, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 6:
                                            LLog.getLog().e("扫描", "6");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 7:
                                            LLog.getLog().e("扫描", "7");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY - 1);
                                            break;
                                        case 8:
                                            LLog.getLog().e("扫描", "8");
                                            robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                                            break;
                                        case 9:
                                            LLog.getLog().e("扫描", "9");
                                            isPrruCollect = false;
                                            iv_operation.setImageResource(R.mipmap.home_start);
                                            if (maxRsrp > -10000f) {
                                                xyRobotWhenMax = realToMap(xWhenMax, yWhenMax);
                                                CollectPointShape maxRsrpPointShape = new CollectPointShape(nowCollectPrru.neCode, R.color.route_color, HomeActivity.this, "dwf");
                                                maxRsrpPointShape.setValues(xyRobotWhenMax[0], xyRobotWhenMax[1]);
                                                map.addShape(maxRsrpPointShape, false);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            case "MoveAction":
                                locVector = platform.searchPath(forwardLocation).getPoints();
                                coorCount = locVector.size();
                                for (int i = 0, len = coorCount; i < len; i++) {
//                            CoorInMap c=new CoorInMap();
                                    float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
//                            c.setX(tf[0]);
//                            c.setY(tf[1]);
//                            coorOrbit.add(c);
                                    CustomShape orbitShape = new CustomShape("coor" + i, R.color.route_color, HomeActivity.this, "dwf", R.mipmap.orbit_point);
                                    orbitShape.setValues(tf[0], tf[1]);
                                    map.addShape(orbitShape, false);
                                }
                                sendEmptyMessageDelayed(0, 2000);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {

                    }
                    break;
                default:
                    break;
            }
        }
    };
    private List<PrruModel> mPrruModelList;
    private TextView tv_home_back;
    private ImageView mSwitch;
    private RockerView mRockerView;


    //防止多个请求同时响应产生的线程不安全问题
    private synchronized void recordMaxRsrp(Float rsrp, float logX, float logY) {
        if (rsrp != null && rsrp - maxRsrp >= 0) {
            xWhenMax = logX - realXo + initX;
            yWhenMax = logY - realYo + initY;
            maxRsrp = rsrp;
        }
    }

    private Float getRsrpByGpp(String gpp, List<PrruSigalModel> prruSigalModelList) {
        for (PrruSigalModel p : prruSigalModelList) {
            if (gpp.equals(p.gpp)) {
                return p.rsrp;
            }
        }
        return null;
    }

    @Override
    public void setContentLayout() {
        mContext = HomeActivity.this;
        setContentView(R.layout.activity_home);
    }


    @Override
    public void dealLogicBeforeInitView() {
        timer.schedule(task, 1000, 4000);
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        tv_setting = findViewById(R.id.tv_setting);
        tv_connect = findViewById(R.id.tv_connect);
        tv_status = findViewById(R.id.tv_status);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        mSwitch = findViewById(R.id.home_change_auto);
        tv_setting.setOnClickListener(this);
        tv_connect.setOnClickListener(this);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        mSwitch.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
//        robotShape = new RobotShape("robot", R.color.blue, HomeActivity.this);

        connection(Constant.firstX, Constant.firstY, Constant.mapScale, Constant.robotIp, Constant.robotPort);
        initRocker();
    }

    private void initShape() {
        cv = new CompassView(HomeActivity.this);
        cv.setId(0);
        cv.setImageResource(R.mipmap.icon_robot);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape = new RequestShape("s", -16776961, cv, HomeActivity.this);
        desShape = new CustomShape("des", R.color.blue, HomeActivity.this, "dwf", R.mipmap.destination_point);

    }

    private void initMap() {
        mPrruModelList = (List<PrruModel>) getIntent().getExtras().getSerializable("PrruModelList");
        mapBitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/"
                + currentMap);
        map.setMapBitmap(mapBitmap);

//        cDemo=new CompassDemo(this,map);
//        cDemo.setPosition(100,100);
        initShape();


        map.setOnRotateListener(new TouchImageView1.OnRotateListener() {
            @Override
            public void onRotate(float rotate) {
                mapRotate = -rotate;
                cv.updateDirection(mapRotate + robotDirection);
                robotShape.setView(cv);
                map.addShape(robotShape, false);
            }
        });

//        cDemo = new CompassDemo(this, map);
//
//        cDemo.setPosition(100,100);
        Log.e("msg", "高度：" + mapBitmap.getHeight() + "，宽度：" + mapBitmap.getWidth());
//        showToast("高度："+mapBitmap.getHeight()+"，宽度："+mapBitmap.getWidth());
//        map.setMapBitmap(BitmapFactory.decodeFile("/sdcard/Tester/成都王府井/成都王府井_3.png"));
        map.setOnLongClickListener1(new TouchImageView1.OnLongClickListener1() {
            @Override
            public void onLongClick(PointF point) {
                Log.e("long", point.x + "," + point.y);
                if (isStart && !isPrruCollect) {
                    float[] dXY = mapToReal(point.x, point.y);
                    robotCancelAndMoveTo(dXY[0], dXY[1]);
//                    showToast("目的点："+dXY[0]+","+dXY[1]);
                    // forwardLocation.setX(dXY[0]);
                    // forwardLocation.setY(dXY[1]);
                    // try {
                    // platform.getCurrentAction().cancel();
                    // for (int i = 0, len = coorCount; i < len; i++) {
                    // map.removeShape("coor" + i);
                    // }
                    // locVector = platform.searchPath(forwardLocation).getPoints();
                    // coorCount = locVector.size();
                    // for (int i = 0, len = coorCount; i < len; i++) {
// //                            CoorInMap c=new CoorInMap();
                    // float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
// //                            c.setX(tf[0]);
// //                            c.setY(tf[1]);
// //                            coorOrbit.add(c);
                    // CollectPointShape collectPointShape = new CollectPointShape("coor" + i, R.color.blue, HomeActivity.this, "dwf");
                    // collectPointShape.setValues(tf[0], tf[1]);
                    // map.addShape(collectPointShape, false);
                    // }
                    // platform.moveTo(locVector);
                    // if (mHandler.hasMessages(0)) {
                    // mHandler.removeMessages(0);
                    // }
                    // mHandler.sendEmptyMessageDelayed(0, 2000);
                    // } catch (Exception e) {
                    // showToast("出错了：" + e.toString());

                    // }
                }
            }
        });
        mapHeight = mapBitmap.getHeight();
//        if (TextUtils.isEmpty(Constant.robotIp)) {
//            Constant.robotIp = SharedPrefHelper.getString(HomeActivity.this, "robotIp", "192.168.11.1");
//        }
//        if (Constant.robotPort == 0) {
//            Constant.robotPort = SharedPrefHelper.getInt(HomeActivity.this, "robotPort", 1445);
//        }
//        Constant.userId = SharedPrefHelper.getString(HomeActivity.this, "userId", "192.168.2.24");
//        Constant.userId = "190.168.2.24";
    }

    private void robotMoveTo(float toX, float toY) {
        getRobotInfo();
        forwardLocation.setX(toX);
        forwardLocation.setY(toY);
        try {
            for (int i = 0, len = coorCount; i < len; i++) {
                map.removeShape("coor" + i);
            }
            locVector = platform.searchPath(forwardLocation).getPoints();
            coorCount = locVector.size();
            for (int i = 0, len = coorCount; i < len; i++) {
//                            CoorInMap c=new CoorInMap();
                float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
//                            c.setX(tf[0]);
//                            c.setY(tf[1]);
//                            coorOrbit.add(c);
                CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, HomeActivity.this, "dwf", R.mipmap.orbit_point);
                orbitShape.setValues(tf[0], tf[1]);
                map.addShape(orbitShape, false);
            }
            desXY = realToMap(toX, toY);
            desShape.setValues(desXY[0], desXY[1]);
            map.addShape(desShape, false);
            platform.moveTo(new Location(toX, toY, initZ));
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } catch (Exception e) {
            showToast("出错了：" + e.toString());

        }

    }

    private float yawToRotation(float yaw) {
        return (float) (yaw * 180 / 3.14);
    }

    private void robotCancelAndMoveTo(float toX, float toY) {
        getRobotInfo();
        forwardLocation.setX(toX);
        forwardLocation.setY(toY);
        try {
            platform.getCurrentAction().cancel();

            for (int i = 0, len = coorCount; i < len; i++) {
                map.removeShape("coor" + i);
            }
            locVector = platform.searchPath(forwardLocation).getPoints();
            coorCount = locVector.size();
            for (int i = 0, len = coorCount; i < len; i++) {
//                            CoorInMap c=new CoorInMap();
                float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
//                            c.setX(tf[0]);
//                            c.setY(tf[1]);
//                            coorOrbit.add(c);
                CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, HomeActivity.this, "dwf", R.mipmap.orbit_point);
                orbitShape.setValues(tf[0], tf[1]);
                map.addShape(orbitShape, false);
            }
            desXY = realToMap(toX, toY);
            desShape.setValues(desXY[0], desXY[1]);
            map.addShape(desShape, false);
            platform.moveTo(new Location(toX, toY, initZ));
            if (mHandler.hasMessages(0)) {
                mHandler.removeMessages(0);
            }
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } catch (Exception e) {
            showToast("出错了：" + e.toString());

        }

    }


    private void initStart(float x, float y) {
        robotShape.setValues(x, y);
        map.addShape(robotShape, false);
//        robotShape.setValues(String.format(
//                "%.5f:%.5f",
//                new Object[]{x,
//                        y}));
//        map.addShape(robotShape, true);
        isStart = true;
        forwardLocation = new Location();
        forwardLocation.setZ(initZ);
        path = new Path();
        lastX = nowX;
        lastY = nowY;
        path.moveTo(x, y);
        lineShape = new LineShape("line", R.color.green, 2, "#00ffba");
    }

    private float[] mapToReal(float x, float y) {
        return new float[]{(x - xo) / scale + initX, (yo - y) / scale + initY};
    }

    private float[] realToMap(float px, float py) {
        return new float[]{(px - initX) * scale + xo, yo - (py - initY) * scale};
    }


    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_home_back:
                disconnectRobot();//释放机器人连接
                finish();
                break;
            case R.id.home_change_auto: //切换操作模式
                if (platform != null) {
                    try {
                        platform.getCurrentAction().cancel();
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
                if (mSwitchAutoFlag == true)//切换为手动
                {
                    mSwitchAutoFlag = false;
                    iv_operation.setVisibility(View.GONE);
                    mRockerView.setVisibility(View.VISIBLE);
                    mSwitch.setImageResource(R.mipmap.home_robot_hand);
                } else {                 //切换为自动
                    mSwitchAutoFlag = true;
                    iv_operation.setVisibility(View.VISIBLE);
                    mRockerView.setVisibility(View.GONE);
                    mSwitch.setImageResource(R.mipmap.home_robot_auto);
                }
                break;
            case R.id.tv_connect:
                initConnectPop(); //初始化连接-弹窗
                break;
            case R.id.iv_operation:
//                openActivity(SettingActivity.class);
                if (!robotConnect) {
                    showToast("机器人未连接");
                    return;
                }
                if (isPrruCollect) {
                    try {
                        platform.getCurrentAction().cancel();
                    } catch (Exception e) {

                    }
                    isPrruCollect = false;
                    iv_operation.setImageResource(R.mipmap.home_start);
                    return;
                }

                if (mPrruModelList != null && mPrruModelList.size() != 0) {
                    initBeginPop(mPrruModelList);     //加载弹窗视图
                } else {
                    showToast("没有Prru列表");
                }

                //
//                /**
//                 * 以下要移动到测试的prruModel
//                 */
//                isPrruCollect=true;
//                iv_operation.setImageResource(R.mipmap.home_stop);
//                nowCollectPrru=new PrruModel();
//                nowCollectPrru.x=6.6f;
//                nowCollectPrru.y=3.9f;
//                //以下写移动逻辑
//                Constant.interRequestUtil.getAllPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getAllPrruInfo?mapId=2046", new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        LLog.getLog().e("getAllPrruInfo成功", s);
//                        AllPrruInfoResponse ap = new Gson().fromJson(s, AllPrruInfoResponse.class);
////                        P lap=new Gson().fromJson(s,LocAndPrruInfoResponse.class);
////                        if(lap.code==0) {
////                            LLog.getLog().prru( "," , prruDataToString(lap.data.prruData));
////                        }
//                        prruModelList = ap.data;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
//                        LLog.getLog().e("prruModelList", prruModelList + "");
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        LLog.getLog().e("getAllPrruInfo错误", volleyError.toString());
//                    }
//                });


//                if (flag) {
//                    iv_operation.setImageResource(R.mipmap.home_start);
//                    tv_status.setText("未连接");
//                    tv_status.setTextColor(getResources().getColor(R.color.route_color));
//                    tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.home_unconnect, 0, 0, 0);
//                    flag = false;
//                } else {
//                    iv_operation.setImageResource(R.mipmap.home_stop);
//                    tv_status.setText("已连接");
//                    tv_status.setTextColor(getResources().getColor(R.color.route_color_active));
//                    tv_status.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.home_connect, 0, 0, 0);
//                    flag = true;
//                }
                break;
            default:
                break;
        }

    }

    private String prruDataToString(List<PrruSigalModel> prruSigalModelList) {
        if (prruSigalModelList == null || prruSigalModelList.size() == 0) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        for (PrruSigalModel p : prruSigalModelList) {
            sb.append(";" + p.toString());
        }
        return sb.substring(1);
    }

    /***
     * 初始化开始popupwindow
     */
    private void initBeginPop(List<PrruModel> prruModelList) {
        mSuperPopupWindow = new SuperPopupWindow(mContext, R.layout.layout_popup_list_choose);
//        mSuperPopupWindow.setFocusable(true);
        mSuperPopupWindow.setOutsideTouchable(true);
        mSuperPopupWindow.setAnimotion(R.style.PopAnimation);
        popupView = mSuperPopupWindow.getPopupView();

        LinearLayout tv_Cancel = popupView.findViewById(R.id.tv_ll_pop_cancel);
        ListView poplist = popupView.findViewById(R.id.pop_list);//获取list对象
        PrruModelListAdapter prruModelListAdapter = new PrruModelListAdapter(mContext, prruModelList);
        poplist.setAdapter(prruModelListAdapter);
        prruModelListAdapter.notifyDataSetChanged();
        prruModelListAdapter.setPrruModelListClickListener(new PrruModelListAdapter.OnPrruModelListClickListener() {
            @Override
            public void onClick(PrruModel prruModel) {
                if (prruModel.x != 0 && prruModel.x != 0) {  //如果 xy 为空 则不让其点击
                    hideBeginPop();
//                    Toast.makeText(HomeActivity.this, prruModel.neId + "", Toast.LENGTH_SHORT).show();
                    //todo 回传点击数据
                    nowCollectPrru = prruModel;
                    isPrruCollect = true;
                    iv_operation.setImageResource(R.mipmap.home_stop);
//                nowCollectPrru=prruModel;
                    nowCollectNeCode = nowCollectPrru.neCode;
                    maxRsrp = Float.NEGATIVE_INFINITY;
                    xWhenMax = Float.NEGATIVE_INFINITY;
                    yWhenMax = Float.NEGATIVE_INFINITY;
                    if ((nowCollectPrru.x - realXo + initX) == nowX && (nowCollectPrru.y - realYo + initY) == nowY) {
                        cStep = 1;
                        LLog.getLog().e("扫描", "1");
                        robotMoveTo(nowCollectPrru.x - realXo + initX + 1, nowCollectPrru.y - realYo + initY);
                    } else {
                        cStep = 0;
                        robotMoveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                    }

                } else {
                    showToast("数据错误！");
                }
            }
        });
        tv_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBeginPop();
            }
        });
        showBeginPop();//默认开始一次
    }

    /**
     * 显示Popupwindow
     */
    private void showBeginPop() {
        mSuperPopupWindow.showPopupWindow();
    }

    /**
     * 隐藏Popupwindow
     */
    private void hideBeginPop() {
        mSuperPopupWindow.hidePopupWindow();
    }


    /***
     * 初始化连接popupwindow
     */
    private void initConnectPop() {
        mConnectPopupWindow = new SuperPopupWindow(mContext, R.layout.layout_connect_pop);
        mConnectPopupWindow.setFocusable(true);
        mConnectPopupWindow.setOutsideTouchable(true);
        mConnectPopupWindow.setAnimotion(R.style.PopAnimation);
        View mConnectPopupView = mConnectPopupWindow.getPopupView();
        initConnectPopView(mConnectPopupView);
        showConnectPop();
    }

    /**
     * 初始化连接弹窗视图
     *
     * @param view
     */
    private void initConnectPopView(View view) {
        TextView tvCancel = view.findViewById(R.id.tv_connect_cancel);         //取消按钮
        TextView tvConfirm = view.findViewById(R.id.tv_connect_confirm);        //确定按钮
        final EditText edtPointX = view.findViewById(R.id.edt_connect_pointX);         //X坐标
        final EditText edtPointY = view.findViewById(R.id.edt_connect_pointY);         //Y坐标
        final EditText edtScale = view.findViewById(R.id.edt_connect_scale);           //比例尺
        final EditText edtRbIp = view.findViewById(R.id.edt_connect_rbip);             //机器人IP
        final EditText edtRbPort = view.findViewById(R.id.edt_connect_rbport);         //机器人端口
        setDefaultConInfo(edtPointX, edtPointY, edtScale, edtRbIp, edtRbPort);       //设置连接信息

        edtScale.setSelection(edtScale.getText().length());
        edtPointX.setSelection(edtPointX.getText().length());
        edtPointY.setSelection(edtPointY.getText().length());
        edtRbIp.setSelection(edtRbIp.getText().length());
        edtRbPort.setSelection(edtRbPort.getText().length());

        lockEditRobotInfo(edtRbIp, edtRbPort);//判断是否锁定机器人ip&&port填写
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideConnectPop();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judgeEmpty(edtPointX, edtPointY, edtScale, edtRbIp, edtRbPort)) {
                    hideConnectPop();
                    float x = Float.parseFloat(edtPointX.getText().toString());
                    float y = Float.parseFloat(edtPointY.getText().toString());
                    float scale = Float.parseFloat(edtScale.getText().toString());

                    //保存信息
                    SharedPrefHelper.putFloat(mContext, "scale", Float.parseFloat(edtScale.getText().toString()));
                    SharedPrefHelper.putFloat(mContext, "realXo", Float.parseFloat(edtPointX.getText().toString()));
                    SharedPrefHelper.putFloat(mContext, "realYo", Float.parseFloat(edtPointY.getText().toString()));
                    SharedPrefHelper.putString(mContext, "robotIp", edtRbIp.getText().toString());
                    SharedPrefHelper.putInt(mContext, "robotPort", Integer.parseInt(edtRbPort.getText().toString()));

                    if (Constant.robotIp == null && Constant.robotPort == 0) //判断是否为空 为空则放值
                    {
                        Constant.robotIp = edtRbIp.getText().toString();
                        Constant.robotPort = Integer.valueOf(edtRbPort.getText().toString());
                    }
                    String robotIp = edtRbIp.getText().toString();
                    int robotPort = Integer.parseInt(edtRbPort.getText().toString());
                    connection(x, y, scale, robotIp, robotPort);                       //进行机器人连接尝试
                }
            }
        });
    }

    /**
     * 判断机器人信息是否填写完善
     *
     * @param x
     * @param y
     * @param scale
     * @param ip
     * @param port
     * @return
     */
    private boolean judgeEmpty(EditText x, EditText y, EditText scale, EditText ip, EditText port) {
        boolean flag = false;
        if (x.getText().toString().trim().equals("")) {
            x.requestFocus();
            showToast("请输入X值");
        } else if (y.getText().toString().trim().equals("")) {
            y.requestFocus();
            showToast("请输入Y值");
        } else if (scale.getText().toString().trim().equals("")) {
            scale.requestFocus();
            showToast("请输入比例尺");
        } else if (ip.getText().toString().trim().equals("")) {
            ip.requestFocus();
            showToast("请输入机器人地址");
        } else if (port.getText().toString().trim().equals("")) {
            port.requestFocus();
            showToast("请输入机器人地址端口");
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 设置默认连接信息
     *
     * @param x
     * @param y
     * @param scale
     * @param ip
     * @param port
     */
    private void setDefaultConInfo(EditText x, EditText y, EditText scale, EditText ip, EditText port) {
        x.setText((String.valueOf(SharedPrefHelper.getFloat(mContext, "realXo", 0))).equals("0.0") ? "" : String.valueOf(SharedPrefHelper.getFloat(mContext, "realXo", 0)));
        y.setText((String.valueOf(SharedPrefHelper.getFloat(mContext, "realYo", 0))).equals("0.0") ? "" : String.valueOf(SharedPrefHelper.getFloat(mContext, "realYo", 0)));
        scale.setText((String.valueOf(SharedPrefHelper.getFloat(mContext, "scale", 0))).equals("0.0") ? "" : String.valueOf(SharedPrefHelper.getFloat(mContext, "scale", 0)));
        ip.setText(SharedPrefHelper.getString(mContext, "robotIp", "0.0.0.0"));
        port.setText(String.valueOf(SharedPrefHelper.getInt(mContext, "robotPort", 0)));

    }

    /**
     * 显示Popupwindow
     */
    private void showConnectPop() {
        mConnectPopupWindow.showPopupWindow();
    }

    /**
     * 隐藏Popupwindow
     */
    private void hideConnectPop() {
        mConnectPopupWindow.hidePopupWindow();
    }

    /**
     * 锁定填写机器人信息
     *
     * @param robotIp   机器人设备ip
     * @param robotPort 机器人设备端口
     */
    private void lockEditRobotInfo(EditText robotIp, EditText robotPort) {
        if (SharedPrefHelper.getBoolean(mContext, "ipFlag")) //存在ip信息锁定robotIp填写
        {
            robotIp.setText(Constant.robotIp);
            robotIp.setEnabled(false);
            robotIp.setTextColor(getResources().getColor(R.color.gray_stroken));
        } else {
            robotIp.setEnabled(true);
            robotIp.setTextColor(getResources().getColor(R.color.white));
        }

        if (SharedPrefHelper.getBoolean(mContext, "ipFlag"))       //存在port信息锁定robotPort填写
        {
            robotPort.setText(String.valueOf(Constant.robotPort));
            robotPort.setEnabled(false);
            robotPort.setTextColor(getResources().getColor(R.color.gray_stroken));
        } else {
            robotPort.setEnabled(true);
            robotPort.setTextColor(getResources().getColor(R.color.white));
        }
    }

    /***
     *
     * @param x
     * @param y
     * @param scaleRuler
     */
    private void saveRbLocationInfo(float x, float y, float scaleRuler) {
        realXo = x;
        realYo = y;
        scale = scaleRuler;
        xo = x * scaleRuler;
        yo = mapHeight - y * scaleRuler;
        SharedPrefHelper.putString(HomeActivity.this, "currentMap", currentMap);
        SharedPrefHelper.putFloat(HomeActivity.this, "scale", scale);
        SharedPrefHelper.putFloat(HomeActivity.this, "xo", xo);
        SharedPrefHelper.putFloat(HomeActivity.this, "yo", yo);
        SharedPrefHelper.putFloat(HomeActivity.this, "realXo", realXo);
        SharedPrefHelper.putFloat(HomeActivity.this, "realYo", realYo);
        initX = nowX;
        initY = nowY;
        SharedPrefHelper.putFloat(HomeActivity.this, "initX", initX);
        SharedPrefHelper.putFloat(HomeActivity.this, "initY", initY);
        initStart(xo, yo);
    }

    private void connection(float pointX, float pointY, float scaleRuler, String robotIp, int robotPort) {
        currentMap = getIntent().getExtras().getString("currentMap");
        try {
            robotConnect = true;
            LLog.getLog().e("连接机器人", robotIp + ":" + robotPort);
            platform = DeviceManager.connect(robotIp, robotPort); // 连接到机器人底盘
            nowPose = platform.getPose();// 当前机器人的位置,
            robotDirection = -yawToRotation(nowPose.getYaw());
            Log.e("dir", robotDirection + "");
            nowX = nowPose.getX();
            nowY = nowPose.getY();
            initZ = nowPose.getZ();
            if (nowX > 0.1f || nowY > 0.1f) {
                if (currentMap.equals(SharedPrefHelper.getString(this, "currentMap", ""))) {
                    isContinue = true;
                } else {
                    isContinue = false;
                }
            } else {
                isContinue = false;
            }
        } catch (Exception e) {
            robotConnect = false;
            e.printStackTrace();
            Log.e("msg", e.toString());
        }
        if (robotConnect) {
            initMap();
//            if (flag) {
//                SharedPrefHelper.putString(HomeActivity.this, "robotIp", robotIp);
//                SharedPrefHelper.putInt(HomeActivity.this, "robotPort", robotPort);
//            }
            showToast("机器人连接成功！");
            if (isContinue) {
                scale = SharedPrefHelper.getFloat(this, "scale");
                initX = SharedPrefHelper.getFloat(this, "initX");
                initY = SharedPrefHelper.getFloat(this, "initY");
                xo = SharedPrefHelper.getFloat(this, "xo");
                yo = SharedPrefHelper.getFloat(this, "yo");
                realXo = SharedPrefHelper.getFloat(this, "realXo");
                realYo = SharedPrefHelper.getFloat(this, "realYo");
                float[] continueXY = realToMap(nowX, nowY);
                initStart(continueXY[0], continueXY[1]);
            } else {
                saveRbLocationInfo(pointX, pointY, scaleRuler);
            }
        } else {
            setResult(-1);
            finish();
//            showToast("机器人连接失败！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LLog.getLog().e("destory", "destroy");
    }

    private void getRobotInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        if (platform != null) {
            try {
                PowerStatus powerStatus = platform.getPowerStatus();
                Pose pose = platform.getPose();
                SleepMode sleepMode = powerStatus.getSleepMode();
                stringBuffer.append("状态------" + sleepMode + "------");
                stringBuffer.append(platform.getBatteryPercentage() + "%电量" + "------");
                stringBuffer.append("Yaw=" + pose.getYaw() + "------");
                stringBuffer.append("Roll=" + pose.getRoll() + "------");
                String s = stringBuffer.toString();
                Log.e("XHF", s);
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
    }

    /**
     * 断开机器人连接
     */
    private void disconnectRobot() {
        if (platform != null) {
            try {
                platform.getCurrentAction().cancel(); //停止机器人动作
                platform.disconnect();                //释放连接
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
    }

    /***
     * 初始化摇杆
     */
    private void initRocker() {
        mRockerView = (RockerView) findViewById(R.id.home_rockerView);
        // 设置回调模式
        mRockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        // 监听摇动方向
//        mRockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
//            @Override
//            public void onStart() {
//                //开始的方法
//            }
//
//            @Override
//            public void angle(double v) {
//                //角度
//                double v1 = Math.toRadians(v);
//                Log.e("XHF","v1="+v);
//            }
//
//            @Override
//            public void onFinish() {
//                //停止
//            }
//        });
        mRockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
                Log.e("XHF", "Start");
                //开始循环监听状态值
                mMoveTimer = new Timer();
                mMoveTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mMoveHandler.sendEmptyMessage(moveCode);
                    }
                }, 1000, 500);
            }

            @Override
            public void direction(RockerView.Direction direction) {
                handRobotMove(direction);
                Log.e("XHF", "摇动方向 : " + getDirection(direction));
            }

            @Override
            public void onFinish() {
                Log.e("XHF", "Finish");
                mMoveTimer.cancel();
            }
        });
    }

    /**
     * 获取遥感的方向
     */
    private String getDirection(RockerView.Direction direction) {
        String directName = "";
        switch (direction) {
            case DIRECTION_UP:                       //方向向上
                directName = "方向向上";
                break;
            case DIRECTION_DOWN:
                directName = "方向向下";
                break;
            case DIRECTION_LEFT:
                directName = "方向向左";
                break;
            case DIRECTION_RIGHT:
                directName = "方向向右";
                break;
            case DIRECTION_CENTER:
                directName = "方向居中";
                break;
            case DIRECTION_UP_LEFT:
                directName = "方向左上";
                break;
            case DIRECTION_UP_RIGHT:
                directName = "方向右上";
                break;
            case DIRECTION_DOWN_LEFT:
                directName = "方向左下";
                break;
            case DIRECTION_DOWN_RIGHT:
                directName = "方向右下";
                break;
            default:
                break;
        }
        return directName;
    }

    private void handRobotMove(RockerView.Direction direction) {
        if (platform != null) {
            switch (direction) {
                case DIRECTION_UP:
                    moveCode = 101;
                    break;
                case DIRECTION_DOWN:
                    moveCode = 102;
                    break;
                case DIRECTION_LEFT:
                    moveCode = 103;
                    break;
                case DIRECTION_RIGHT:
                    moveCode = 104;
                    break;
                case DIRECTION_CENTER:
                    break;
                case DIRECTION_UP_LEFT:
                    break;
                case DIRECTION_UP_RIGHT:
                    break;
                case DIRECTION_DOWN_LEFT:
                    break;
                case DIRECTION_DOWN_RIGHT:
                    break;
                default:
                    break;
            }
        }
    }

    private void robotMove1() {

    }
}
