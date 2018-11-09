package com.chinasoft.robotdemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.util.FileUtil;
import com.chinasoft.robotdemo.view.dialog.ParamsDialog;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.MoniPointShape;
import net.yoojia.imagemap.core.PushMessageShape;
import net.yoojia.imagemap.core.RobotShape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.core.SpecialShape;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class HomeActivity extends BaseActivity {

    private ImageMap1 map;

    private ParamsDialog paramsDialog;

    private AbstractSlamwarePlatform platform;

    private Pose nowPose;

    private int mapWidth, mapHeight;

    private boolean robotConnect = true;

    private boolean isStart = false;

    private float xo, yo, scale, initX, initY, initZ;

    private RobotShape robotShape;

    private Location forwardLocation;

    private Vector<Location> locVector;

//    private List<CoorInMap> coorOrbit=new ArrayList<>();

    private int coorCount = 0;

    private IAction nowAction;

    private LineShape line;

    private Path path;

    private LineShape lineShape;

    private float lastX, lastY,nowX,nowY;

    private  float[] oldF,newF;

    private Bitmap mapBitmap;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        nowPose = platform.getPose();
//                        Log.e("msg", "x:" + nowPose.getX() + ",y:" + nowPose.getY());
                        nowX=nowPose.getX();
                        nowY=nowPose.getY();
                        if (nowX - lastX > 0.5 || nowY - lastY > 0.5) {
                            newF = realToMap(nowX, nowY);
                            path.lineTo(newF[0], newF[1]);
                            lineShape.setPath(path);
                            map.addShape(lineShape, false);
                            lastX=nowX;
                            lastY=nowY;
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
                                Log.e("msg","aaa");
                                    Log.e("msg","bbb");
                                    removeMessages(0);
                                    newF = realToMap(nowX, nowY);
                                    path.lineTo(newF[0], newF[1]);
                                    lineShape.setPath(path);
                                    map.addShape(lineShape, false);
                                    lastX=nowX;
                                    lastY=nowY;
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
    }

    @Override
    public void dealLogicAfterInitView() {
//        mapBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.f1_100);
//        map.setMapDrawable(getResources().getDrawable(R.mipmap.f1_100));
        File dir = new File("/sdcard/test");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File mapFile = new File("/sdcard/test/test.png");
        if (!mapFile.exists()) {
            try {
                mapFile.createNewFile();
                FileUtil.writeBytesToFile(this.getAssets().open("test.png"), mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapBitmap = BitmapFactory.decodeFile("/sdcard/test/test.png");
        map.setMapBitmap(mapBitmap);
        Log.e("msg", "高度：" + mapBitmap.getHeight() + "，宽度：" + mapBitmap.getWidth());
//        showToast("高度："+mapBitmap.getHeight()+"，宽度："+mapBitmap.getWidth());
//        map.setMapBitmap(BitmapFactory.decodeFile("/sdcard/Tester/成都王府井/成都王府井_3.png"));
        map.setOnLongClickListener1(new TouchImageView1.OnLongClickListener1() {
            @Override
            public void onLongClick(PointF point) {
                Log.e("long", point.x + "," + point.y);
                if (isStart) {
//                    if (canMove) {
//                    showToast("像素点："+x + "," + y);
//                    robotShape.setValues(String.format(
//                            "%.5f:%.5f",
//                            new Object[] { x,
//                                    y}));
//                    map.addShape(robotShape,false);
//                    lineShape.setValues(x,  y, oldX, oldY);
////                    path.reset();
//                    path.moveTo(oldX,oldY);
//                    path.lineTo(x,y);
//                    lineShape.setPath(path);
//                    map.addShape(lineShape, false);
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
//                    } else {
//                        canMove = true;
//                        mHandler.sendEmptyMessageDelayed(1, 1000);
//                    }
                }
            }
        });
//        map.setOnShapeClickListener(new ShapeExtension.OnShapeActionListener() {
//            @Override
//            public void onSpecialShapeClick(SpecialShape specialShape, float v, float v1) {
//
//            }
//
//            @Override
//            public void onPushMessageShapeClick(PushMessageShape pushMessageShape, float v, float v1) {
//
//            }
//
//            @Override
//            public void onCollectShapeClick(CollectPointShape collectPointShape, float v, float v1) {
//
//            }
//
//            @Override
//            public void onMoniShapeClick(MoniPointShape moniPointShape, float v, float v1) {
//
//            }
//
//            @Override
//            public void outShapeClick(float x, float y) {
//                if(isStart) {
////                    showToast("像素点："+x + "," + y);
////                    robotShape.setValues(String.format(
////                            "%.5f:%.5f",
////                            new Object[] { x,
////                                    y}));
////                    map.addShape(robotShape,false);
////                    lineShape.setValues(x,  y, oldX, oldY);
//////                    path.reset();
////                    path.moveTo(oldX,oldY);
////                    path.lineTo(x,y);
////                    lineShape.setPath(path);
////                    map.addShape(lineShape, false);
//                        float[] dXY = mapToReal(x, y);
////                    showToast("目的点："+dXY[0]+","+dXY[1]);
//                        forwardLocation.setX(dXY[0]);
//                        forwardLocation.setY(dXY[1]);
//                        try {
//                            platform.getCurrentAction().cancel();
//                            for (int i = 0, len = coorCount; i < len; i++) {
//                                map.removeShape("coor" + i);
//                            }
//                            locVector = platform.searchPath(forwardLocation).getPoints();
//                            coorCount = locVector.size();
//                            for (int i = 0, len = coorCount; i < len; i++) {
////                            CoorInMap c=new CoorInMap();
//                                float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
////                            c.setX(tf[0]);
////                            c.setY(tf[1]);
////                            coorOrbit.add(c);
//                                CollectPointShape collectPointShape = new CollectPointShape("coor" + i, R.color.blue, HomeActivity.this, "dwf");
//                                collectPointShape.setValues(tf[0], tf[1]);
//                                map.addShape(collectPointShape, false);
//                            }
//                            platform.moveTo(locVector);
//                            if (mHandler.hasMessages(0)) {
//                                mHandler.removeMessages(0);
//                            }
//                            mHandler.sendEmptyMessageDelayed(0, 2000);
//                        } catch (Exception e) {
//                            showToast("出错了：" + e.toString());
//
//                        }
//
//                }
//            }
//        });
        try {
            platform = DeviceManager.connect("192.168.11.1", 1445); // 连接到机器人底盘
            nowPose = platform.getPose();// 当前机器人的位置,
//            System.out.println(nowPose.getX() + "," + nowPose.getY());
            initX = nowPose.getX();
            initY = nowPose.getY();
            initZ = nowPose.getZ();
//            showToast("初始位置："+initX+","+initY+","+initZ);
        } catch (Exception e) {
            robotConnect = false;
            e.printStackTrace();
        }
        if (robotConnect) {
            paramsDialog = new ParamsDialog(this, R.style.MyDialogStyle);
            paramsDialog.setOnDialogListener(new ParamsDialog.OnDialogStartCollectListener() {
                @Override
                public void paramsComplete(float x, float y, float scaleRuler) {
//                    Location location=new Location(50f,20f,nowPose.getZ());
//                    try {
//                        platform.moveTo(location);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    isStart = true;
                    mapHeight = mapBitmap.getHeight();
//                    showToast("高度："+mapHeight+"，宽度："+mapBitmap.getWidth());
                    forwardLocation = new Location();
                    forwardLocation.setZ(initZ);
//                    canMove = true;
                    scale = scaleRuler;
                    xo = x * scaleRuler;
                    yo = mapHeight - y * scaleRuler;
                    path = new Path();
                    lastX = initX;
                    lastY = initY;
                    oldF = realToMap(lastX, lastY);
                    path.moveTo(oldF[0], oldF[1]);
                    lineShape = new LineShape("line", R.color.green);
                    robotShape = new RobotShape("robot", R.color.blue, HomeActivity.this);
                    robotShape.setValues(String.format(
                            "%.5f:%.5f",
                            new Object[]{xo,
                                    yo}));
//                    black.setId(tempModel.id + "");
                    map.addShape(robotShape, true);
                }
            });
            paramsDialog.show();
        } else {
            showToast("机器人连接失败！");
        }

    }

    private float[] mapToReal(float x, float y) {
        return new float[]{(x - xo) / scale + initX, (yo - y) / scale + initY};
    }

    private float[] realToMap(float px, float py) {
        return new float[]{(px - initX) * scale + xo, yo - (py - initY) * scale};
    }

    @Override
    public void onClickEvent(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
