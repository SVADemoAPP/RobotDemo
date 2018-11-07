package com.chinasoft.robotdemo.activity;

import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.view.dialog.ParamsDialog;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Pose;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.MoniPointShape;
import net.yoojia.imagemap.core.PushMessageShape;
import net.yoojia.imagemap.core.RobotShape;
import net.yoojia.imagemap.core.ShapeExtension;
import net.yoojia.imagemap.core.SpecialShape;
import net.yoojia.imagemap.core.pRRUInfoShape;

public class HomeActivity extends BaseActivity {

    private ImageMap1 map;

    private ParamsDialog paramsDialog;

    private AbstractSlamwarePlatform platform;

    private Pose nowPose;

    private boolean robotConnect = true;

    private boolean canMove=false;

    private float xo,yo,scale,oldX,oldY,initX,initY,initZ;

    private RobotShape robotShape;

    private Location forwardLocation;

    private LineShape line;

    private  Path path;

    private LineShape lineShape;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
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
        map.setMapDrawable(getResources().getDrawable(R.mipmap.chinasoft_1f_scale100));
//        map.setMapBitmap(BitmapFactory.decodeFile("/sdcard/Tester/成都王府井/成都王府井_3.png"));
        map.setOnShapeClickListener(new ShapeExtension.OnShapeActionListener() {
            @Override
            public void onSpecialShapeClick(SpecialShape specialShape, float v, float v1) {

            }

            @Override
            public void onPushMessageShapeClick(PushMessageShape pushMessageShape, float v, float v1) {

            }

            @Override
            public void onCollectShapeClick(CollectPointShape collectPointShape, float v, float v1) {

            }

            @Override
            public void onMoniShapeClick(MoniPointShape moniPointShape, float v, float v1) {

            }

            @Override
            public void outShapeClick(float x, float y) {
                if(canMove){
//                    showToast("像素点："+x + "," + y);
                    robotShape.setValues(String.format(
                            "%.5f:%.5f",
                            new Object[] { x,
                                    y}));
                    map.addShape(robotShape,false);
                    lineShape.setValues(x,  y, oldX, oldY);
//                    path.reset();
                    path.moveTo(oldX,oldY);
                    path.lineTo(x,y);
                    lineShape.setPath(path);
                    map.addShape(lineShape, false);
                    float dX=(x-xo)/scale+initX;
                    float dY=(y-yo)/scale+initY;
                    showToast("目的点："+dX+","+dY);
                    forwardLocation.setX(dX);
                    forwardLocation.setY(dY);
//                    try {
//                        platform.moveTo(forwardLocation);
//                    } catch (Exception e) {
//                    }
                    oldX=x;
                    oldY=y;
                }
            }
        });
        try {
//            platform = DeviceManager.connect("192.168.11.11", 1445); // 连接到机器人底盘
//            nowPose = platform.getPose();// 当前机器人的位置,
////            System.out.println(nowPose.getX() + "," + nowPose.getY());
//            initX=nowPose.getX();
//            initY=nowPose.getY();
//            initZ=nowPose.getZ();
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
                    forwardLocation=new Location();
                    forwardLocation.setZ(initZ);
                    canMove=true;
                    xo=x;
                    yo=y;
                    oldX=x;
                    oldY=y;
                    scale=scaleRuler;
                    path=new Path();
                    lineShape = new LineShape("line", R.color.green);
                    robotShape = new RobotShape("robot", R.color.blue, HomeActivity.this);
                    robotShape.setValues(String.format(
                            "%.5f:%.5f",
                            new Object[] { x,
                                    y}));
//                    black.setId(tempModel.id + "");
                    map.addShape(robotShape, false);
                }
            });
            paramsDialog.show();
        } else {
            showToast("机器人连接失败！");
        }

    }

    @Override
    public void onClickEvent(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
