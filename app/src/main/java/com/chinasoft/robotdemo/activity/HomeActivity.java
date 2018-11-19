package com.chinasoft.robotdemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.bean.BaseReponse;
import com.chinasoft.robotdemo.bean.LocAndPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.bean.PrruSigalModel;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.view.dialog.ParamsDialog;
import com.chinasoft.robotdemo.view.dialog.RobotparamDialog;
import com.google.gson.Gson;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.RobotShape;

import java.util.List;
import java.util.Vector;

public class HomeActivity extends BaseActivity {

    private ImageMap1 map;

    private ParamsDialog paramsDialog;

    private RobotparamDialog robotparamDialog;

    private AbstractSlamwarePlatform platform;

    private Pose nowPose;

    private int mapWidth, mapHeight;

    private boolean robotConnect = true;

    private boolean isStart = false;

    private float xo, yo, scale, initX, initY, initZ,realXo,realYo;

    private RobotShape robotShape;

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

    private boolean flag = false;

    private List<PrruModel> prruModelList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        nowPose = platform.getPose();
//                        Log.e("msg", "x:" + nowPose.getX() + ",y:" + nowPose.getY());
                        nowX = nowPose.getX();
                        nowY = nowPose.getY();
                        final float logX=nowX+realXo;
                        final float logY=nowY+realYo;
                        Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + Constant.userId + "&mapId=1", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                LLog.getLog().e("getLocAndPrruInfo成功",s);
                                LocAndPrruInfoResponse lap=new Gson().fromJson(s,LocAndPrruInfoResponse.class);
                                if(lap.code==0) {
                                    LLog.getLog().prru(logX + "," + logY, prruDataToString(lap.data.prruData));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                LLog.getLog().e("getLocAndPrruInfo错误",volleyError.toString());
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
                                lastX = nowX;
                                lastY = nowY;
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
                                    CollectPointShape collectPointShape = new CollectPointShape("coor" + i, R.color.blue, HomeActivity.this, "dwf");
                                    collectPointShape.setValues(tf[0], tf[1]);
                                    map.addShape(collectPointShape, false);
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


    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_home);
    }


    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        tv_setting = findViewById(R.id.tv_setting);
        tv_connect = findViewById(R.id.tv_connect);
        tv_status = findViewById(R.id.tv_status);
        iv_operation = findViewById(R.id.iv_operation);
        tv_setting.setOnClickListener(this);
        tv_connect.setOnClickListener(this);
        iv_operation.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
//        mapBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.f1_100);
//        map.setMapDrawable(getResources().getDrawable(R.mipmap.f1_100));
        currentMap = getIntent().getExtras().getString("currentMap");
        mapBitmap = BitmapFactory.decodeFile(Constant.mapDirs + currentMap);
        map.setMapBitmap(mapBitmap);
        Log.e("msg", "高度：" + mapBitmap.getHeight() + "，宽度：" + mapBitmap.getWidth());
//        showToast("高度："+mapBitmap.getHeight()+"，宽度："+mapBitmap.getWidth());
//        map.setMapBitmap(BitmapFactory.decodeFile("/sdcard/Tester/成都王府井/成都王府井_3.png"));
        map.setOnLongClickListener1(new TouchImageView1.OnLongClickListener1() {
            @Override
            public void onLongClick(PointF point) {
                Log.e("long", point.x + "," + point.y);
                if (isStart) {
                    float[] dXY = mapToReal(point.x, point.y);
//                    showToast("目的点："+dXY[0]+","+dXY[1]);
                    forwardLocation.setX(dXY[0]);
                    forwardLocation.setY(dXY[1]);
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
                            CollectPointShape collectPointShape = new CollectPointShape("coor" + i, R.color.blue, HomeActivity.this, "dwf");
                            collectPointShape.setValues(tf[0], tf[1]);
                            map.addShape(collectPointShape, false);
                        }
                        platform.moveTo(locVector);
                        if (mHandler.hasMessages(0)) {
                            mHandler.removeMessages(0);
                        }
                        mHandler.sendEmptyMessageDelayed(0, 2000);
                    } catch (Exception e) {
                        showToast("出错了：" + e.toString());

                    }
                }
            }
        });
        mapHeight = mapBitmap.getHeight();
        if (TextUtils.isEmpty(Constant.robotIp)) {
            Constant.robotIp = SharedPrefHelper.getString(HomeActivity.this, "robotIp", "192.168.11.1");
        }
        if (Constant.robotPort == 0) {
            Constant.robotPort = SharedPrefHelper.getInt(HomeActivity.this, "robotPort", 1445);
        }
        Constant.userId = SharedPrefHelper.getString(HomeActivity.this, "userId", "192.168.1.1");
    }

    private void initStart(float x, float y) {
        robotShape = new RobotShape("robot", R.color.blue, HomeActivity.this);
        robotShape.setValues(String.format(
                "%.5f:%.5f",
                new Object[]{x,
                        y}));
        map.addShape(robotShape, true);
        isStart = true;
        forwardLocation = new Location();
        forwardLocation.setZ(initZ);
        path = new Path();
        lastX = nowX;
        lastY = nowY;
        path.moveTo(x, y);
        lineShape = new LineShape("line", R.color.green);
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
            case R.id.tv_setting:
//                openActivity(SettingActivity.class);
                if (robotparamDialog == null) {
                    robotparamDialog = new RobotparamDialog(this, R.style.MyDialogStyle);
                    robotparamDialog.setOnRobotparamListener(new RobotparamDialog.OnRobotparamListener() {
                        @Override
                        public void paramsComplete(String ip, int port, String userId) {
                            SharedPrefHelper.putString(HomeActivity.this, "robotIp", ip);
                            SharedPrefHelper.putInt(HomeActivity.this, "robotPort", port);
                            SharedPrefHelper.putString(HomeActivity.this, "userId", userId);
                            Constant.robotIp = ip;
                            Constant.robotPort = port;
                            Constant.userId = userId;
                        }
                    });
                }
                robotparamDialog.show();
                robotparamDialog.setData(SharedPrefHelper.getString(HomeActivity.this, "robotIp",
                        "192.168.11.1"), SharedPrefHelper.getInt(HomeActivity.this, "robotPort",
                        1445), SharedPrefHelper.getString(HomeActivity.this, "userId",
                        "192.168.1.1"));
                break;
            case R.id.tv_connect:
                try {
                    robotConnect=true;
                    LLog.getLog().e("连接机器人",Constant.robotIp+":"+Constant.robotPort);
                    platform = DeviceManager.connect(Constant.robotIp, Constant.robotPort); // 连接到机器人底盘

                    nowPose = platform.getPose();// 当前机器人的位置,
//            System.out.println(nowPose.getX() + "," + nowPose.getY());
                    nowX = nowPose.getX();
                    nowY = nowPose.getY();
                    initZ = nowPose.getZ();
//            showToast("初始位置："+initX+","+initY+","+initZ);
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
                }
                if (robotConnect) {
                    showToast("机器人连接成功！");
                    if (isContinue) {
                        scale = SharedPrefHelper.getFloat(this, "scale");
                        initX = SharedPrefHelper.getFloat(this, "initX");
                        initY = SharedPrefHelper.getFloat(this, "initY");
                        xo = SharedPrefHelper.getFloat(this, "xo");
                        yo = SharedPrefHelper.getFloat(this, "yo");
                        realXo=SharedPrefHelper.getFloat(this, "realXo");
                        realYo=SharedPrefHelper.getFloat(this, "realYo");
                        float[] continueXY = realToMap(nowX, nowY);
                        initStart(continueXY[0], continueXY[1]);
                    } else {
                        if (paramsDialog == null) {
                            paramsDialog = new ParamsDialog(this, R.style.MyDialogStyle);
                            paramsDialog.setOnDialogListener(new ParamsDialog.OnDialogStartCollectListener() {
                                @Override
                                public void paramsComplete(float x, float y, float scaleRuler) {
                                    realXo=x;
                                    realYo=y;
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
                            });
                        }
                        paramsDialog.show();
                    }
                } else {
                    showToast("机器人连接失败！");
                }
                break;
            case R.id.iv_operation:
                Constant.interRequestUtil.getAllPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getAllPrruInfo?mapId=2046", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LLog.getLog().e("getAllPrruInfo成功",s);
//                        P lap=new Gson().fromJson(s,LocAndPrruInfoResponse.class);
//                        if(lap.code==0) {
//                            LLog.getLog().prru( "," , prruDataToString(lap.data.prruData));
//                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        LLog.getLog().e("getAllPrruInfo错误",volleyError.toString());
                    }
                });
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

    private String prruDataToString(List<PrruSigalModel> prruSigalModelList){
        if(prruSigalModelList==null||prruSigalModelList.size()==0){
            return "null";
        }
        StringBuffer sb=new StringBuffer();
        for(PrruSigalModel p:prruSigalModelList){
            sb.append(";"+p.toString());
        }
        return sb.substring(1);
    }

}
