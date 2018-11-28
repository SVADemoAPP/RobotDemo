package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
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
import com.chinasoft.robotdemo.robot.OnRobotListener;
import com.chinasoft.robotdemo.robot.RobotOperation;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.util.RobotMoveUtils;
import com.chinasoft.robotdemo.util.SuperPopupWindow;
import com.chinasoft.robotdemo.view.CompassView;
import com.google.gson.Gson;
import com.kongqw.rockerlibrary.view.RockerView;
import com.slamtec.slamware.robot.Location;
import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.RequestShape;
import java.util.List;
import java.util.Vector;

public class PrrufindActivity extends BaseActivity implements OnRobotListener {
    private ImageMap1 map;
    private int mapHeight;
    private boolean isStart = false;
    private boolean isPrruCollect = false;
    private boolean isAutoFind = false;
    private PrruModel nowCollectPrru;
    private float xo, yo, scale, initX, initY, initZ, realXo, realYo;
    private int coorCount = 0;
    private Path path;
    private LineShape lineShape;
    private float lastX, lastY, nowX, nowY;
    private float[] newF,desF,pXY;
    private String currentMap;
    private TextView tv_setting;
    private ImageView iv_operation;
    private RequestShape robotShape;
    private CompassView cv;
    private int cStep;
    private String nowCollectNeCode;
    private float maxRsrp, xWhenMax, yWhenMax;
    private float[] xyRobotWhenMax;
    private CustomShape desShape;
    private SuperPopupWindow mSuperPopupWindow;
    private Context mContext;
    private View popupView;
    private float mapRotate;
    private float robotDirection;
    private RobotOperation ro;
    private boolean mSwitchAutoFlag = true; //默认为自动

//    private int moveCode;   //
//    private Handler mMoveHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (platform != null) {
//                try {
//                    switch (msg.what) {
//                        case 101:
//                            platform.moveBy(MoveDirection.FORWARD);
////                        platform.rotateTo(new Rotation((float) Math.PI/4));
//                            break;
//                        case 102:
//                            platform.moveBy(MoveDirection.BACKWARD);
//                            break;
//                        case 103:
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        moveCode = 100;
//                                        IMoveAction iMoveAction = platform.moveBy(MoveDirection.TURN_LEFT);
//                                        iMoveAction.waitUntilDone();
//                                        moveCode = 101;
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
////                            getRobotInfo();
//                            break;
//                        case 104:
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        moveCode = 100;
//                                        IMoveAction iMoveAction = platform.moveBy(MoveDirection.TURN_RIGHT);
//                                        iMoveAction.waitUntilDone();
//                                        moveCode = 101;
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
//    Timer mMoveTimer;


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
        mContext = PrrufindActivity.this;
        setContentView(R.layout.activity_prrufind);
    }


    @Override
    public void dealLogicBeforeInitView() {
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        tv_setting = findViewById(R.id.tv_setting);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        mSwitch = findViewById(R.id.home_change_auto);
        tv_setting.setOnClickListener(this);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        mSwitch.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
        ro = new RobotOperation(Constant.robotIp, Constant.robotPort, this);
        ro.startOperation();
//        initRocker();
    }

    private void initShape() {
        cv = new CompassView(PrrufindActivity.this);
        cv.setId(0);
        cv.setImageResource(R.mipmap.icon_robot);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape = new RequestShape("s", -16776961, cv, PrrufindActivity.this);
        desShape = new CustomShape("des", R.color.blue, PrrufindActivity.this, "dwf", R.mipmap.destination_point);
    }

    private void initMap() {
        mPrruModelList = (List<PrruModel>) getIntent().getExtras().getSerializable("PrruModelList");
        map.setMapBitmap(Constant.mapBitmap);
        initShape();
        map.setOnRotateListener(new TouchImageView1.OnRotateListener() {
            @Override
            public void onRotate(float rotate) {
                mapRotate = -rotate;
                cv.updateDirection(mapRotate + robotDirection);
                robotShape.setView(cv);
            }
        });

        map.setOnLongClickListener1(new TouchImageView1.OnLongClickListener1() {
            @Override
            public void onLongClick(PointF point) {
                if (isStart && !isAutoFind) {
                    float[] realXY = mapToReal(point.x, point.y);
                    ro.cancelAndMoveTo(realXY[0], realXY[1]);
                    desShape.setValues(point.x, point.y);
                    map.addShape(desShape, false);
                }
            }
        });
        mapHeight = Constant.mapBitmap.getHeight();
    }




    private void initStart(float x, float y) {
        robotShape.setValues(x, y);
        map.addShape(robotShape, false);
        isStart = true;
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
                finish();
                break;
            case R.id.home_change_auto: //切换操作模式
                ro.forceStop();
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
            case R.id.iv_operation:
                if (isAutoFind) {
                    ro.forceStop();
                    isAutoFind = false;
                    iv_operation.setImageResource(R.mipmap.home_start);
                    return;
                }
                if (mPrruModelList != null && mPrruModelList.size() != 0) {
                    initBeginPop(mPrruModelList);     //加载弹窗视图
                } else {
                    showToast("没有Prru列表");
                }
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
                    nowCollectPrru = prruModel;
                    nowCollectPrru.x=1.2f;
                    nowCollectPrru.y=0.7f;
                    desF=realToMap(nowCollectPrru.x-realXo,nowCollectPrru.y-realYo);
                    desShape.setValues(desF[0], desF[1]);
                    map.addShape(desShape, false);
                    isAutoFind = true;
                    iv_operation.setImageResource(R.mipmap.home_stop);
                    nowCollectNeCode = nowCollectPrru.neCode;
                    maxRsrp = Float.NEGATIVE_INFINITY;
                    xWhenMax = Float.NEGATIVE_INFINITY;
                    yWhenMax = Float.NEGATIVE_INFINITY;
                    if ((nowCollectPrru.x - realXo + initX) == nowX && (nowCollectPrru.y - realYo + initY) == nowY) {
                        cStep = 1;
                        LLog.getLog().e("扫描", "1");
                        ro.moveTo(nowCollectPrru.x - realXo + initX + 0.5f, nowCollectPrru.y - realYo + initY);
                    } else {
                        cStep = 0;
                        ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
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
        SharedPrefHelper.putString(PrrufindActivity.this, "currentMap", currentMap);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "scale", scale);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "xo", xo);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "yo", yo);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "realXo", realXo);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "realYo", realYo);
        initX = nowX;
        initY = nowY;
        SharedPrefHelper.putFloat(PrrufindActivity.this, "initX", initX);
        SharedPrefHelper.putFloat(PrrufindActivity.this, "initY", initY);
        initStart(xo, yo);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ro != null) {
            ro.endOperation();
            ro.disconnect();
        }
    }


    /***
     * 初始化摇杆
     */
    private void initRocker() {
        mRockerView = findViewById(R.id.home_rockerView);
        // 设置回调模式
        mRockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        //监听摇动方向
        mRockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {
                //开始的方法
//                RobotMoveUtils.goStraight(0);
            }

            @Override
            public void angle(double v) {
                //角度
//               RobotMoveUtils.setRobotMove(platform,RobotMoveUtils.fuzzyDirection(v), (HomeActivity) mContext);
            }

            @Override
            public void onFinish() {
                RobotMoveUtils.goStraight(0);
                //停止
            }
        });
        mRockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
                Log.e("XHF", "Start");
                //开始循环监听状态值
//                mMoveT？
            }

            @Override
            public void direction(RockerView.Direction direction) {
//                handRobotMove(direction);
                Log.e("XHF", "摇动方向 : " + getDirection(direction));
            }

            @Override
            public void onFinish() {
                Log.e("XHF", "Finish");
//                mMoveTimer.cancel();
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

//    private void handRobotMove(RockerView.Direction direction) {
//        if (platform != null) {
//            switch (direction) {
//                case DIRECTION_UP:
//                    moveCode = 101;
//                    break;
//                case DIRECTION_DOWN:
//                    moveCode = 102;
//                    break;
//                case DIRECTION_LEFT:
//                    moveCode = 103;
//                    break;
//                case DIRECTION_RIGHT:
//                    moveCode = 104;
//                    break;
//                case DIRECTION_CENTER:
//                    break;
//                case DIRECTION_UP_LEFT:
//                    break;
//                case DIRECTION_UP_RIGHT:
//                    break;
//                case DIRECTION_DOWN_LEFT:
//                    break;
//                case DIRECTION_DOWN_RIGHT:
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    private long lastClickTime;

    @Override
    public void onBackPressed() {
        long nowClickTime=System.currentTimeMillis();
        if(nowClickTime-lastClickTime>2000){
            lastClickTime=nowClickTime;
            showToast("再点一次回退");
        }else{
            finish();
        }
    }

    private void autoFind() {
        cStep++;
        switch (cStep) {
            case 1:
                LLog.getLog().e("扫描", "1");
                ro.moveTo(nowCollectPrru.x - realXo + initX + 0.5f, nowCollectPrru.y - realYo + initY);
                break;
            case 2:
                LLog.getLog().e("扫描", "2");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                break;
            case 3:
                LLog.getLog().e("扫描", "3");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY + 0.5f);
                break;
            case 4:
                LLog.getLog().e("扫描", "4");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                break;
            case 5:
                LLog.getLog().e("扫描", "5");
                ro.moveTo(nowCollectPrru.x - realXo + initX - 0.5f, nowCollectPrru.y - realYo + initY);
                break;
            case 6:
                LLog.getLog().e("扫描", "6");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                break;
            case 7:
                LLog.getLog().e("扫描", "7");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY - 0.5f);
                break;
            case 8:
                LLog.getLog().e("扫描", "8");
                ro.moveTo(nowCollectPrru.x - realXo + initX, nowCollectPrru.y - realYo + initY);
                break;
            case 9:
                LLog.getLog().e("扫描", "9");
                isAutoFind = false;
                arriveDes();
                iv_operation.setImageResource(R.mipmap.home_start);
                if (maxRsrp > -10000f) {
                    xyRobotWhenMax = realToMap(xWhenMax, yWhenMax);
                    CollectPointShape maxRsrpPointShape = new CollectPointShape(nowCollectPrru.neCode, R.color.route_color, PrrufindActivity.this, "dwf");
                    maxRsrpPointShape.setValues(xyRobotWhenMax[0], xyRobotWhenMax[1]);
                    map.addShape(maxRsrpPointShape, false);
                }
                break;
            default:
                break;
        }
    }

    //移除轨迹点
    private void clearOrbits(){
        for (int i = 0, len = coorCount; i < len; i++) {
            map.removeShape("coor" + i);
        }
    }

    //到达目的点
    private void arriveDes(){
        newF = realToMap(nowX, nowY);
        path.lineTo(newF[0], newF[1]);
        lineShape.setPath(path);
        map.addShape(lineShape, false);
        lastX = nowX;
        lastY = nowY;
        map.removeShape("des");
        updateRobotByReal();
    }

    //根据实际坐标更新机器人图标
    private synchronized void updateRobotByReal(){
        pXY = realToMap(nowX, nowY);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape.setView(cv);
        robotShape.setValues(pXY[0], pXY[1]);
    }

    @Override
    public void connectSuccess(float x, float y, float z, float direc) {
        showToast("机器人连接成功！");
        initMap();
        currentMap = getIntent().getExtras().getString("currentMap");
        nowX = x;
        nowY = y;
        initZ = z;
        robotDirection = direc;
        if ((nowX > 0.1f || nowY > 0.1f) && currentMap.equals(SharedPrefHelper.getString(this, "currentMap", ""))) {
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
            saveRbLocationInfo(Constant.firstX, Constant.firstY, Constant.mapScale);
        }
    }

    @Override
    public void connectFailed(String errormsg) {
        showToast("机器人连接失败！");
        finish();
    }

    @Override
    public void catchError(String errormsg) {
        showToast("异常断开");
        finish();
    }

    @Override
    public void refreshOrbits(Vector<Location> locVector) {
        clearOrbits();
        coorCount = locVector.size();
        for (int i = 0, len = coorCount; i < len; i++) {
            float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
            CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, PrrufindActivity.this, "dwf", R.mipmap.orbit_point);
            orbitShape.setValues(tf[0], tf[1]);
            map.addShape(orbitShape, false);
        }
    }


    @Override
    public void notifyPrru(float x, float y) {
        if(!isPrruCollect){
            return;
        }
        final float logX = x - initX + realXo;
        final float logY = y - initY + realYo;
        Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + Constant.userId + "&mapId=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LLog.getLog().e("getLocAndPrruInfo成功", s);
                LocAndPrruInfoResponse lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                if (lap.code == 0) {
                    LLog.getLog().prru(logX + "," + logY, prruDataToString(lap.data.prruData));
                    if (isAutoFind) {
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
    }

    @Override
    public void positionChange(float x, float y, float direc) {
        nowX = x;
        nowY = y;
        robotDirection = direc;
        if (Math.sqrt((nowX - lastX) * (nowX - lastX) + (nowY - lastY) * (nowY - lastY)) > Constant.lineSpace) {
//                            Log.e("handler","的点点滴滴顶顶顶顶顶顶顶顶顶大等等");
            newF = realToMap(nowX, nowY);
            path.lineTo(newF[0], newF[1]);
            lineShape.setPath(path);
            map.addShape(lineShape, false);
            lastX = nowX;
            lastY = nowY;
        }
        updateRobotByReal();
    }

    @Override
    public void moveFinish(float x, float y, float direc,boolean isForce) {
        nowX=x;
        nowY=y;
        robotDirection=direc;
        clearOrbits();
        if(isAutoFind&&!isForce) {
            autoFind();
        }else{
            arriveDes();
        }
    }



}
