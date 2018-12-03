package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.UserIdAdapter;
import com.chinasoft.robotdemo.bean.LocAndPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.bean.PrruSigalModel;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.robot.OnRobotListener;
import com.chinasoft.robotdemo.robot.RobotOperation;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.view.MyListView;
import com.chinasoft.robotdemo.view.popup.SuperPopupWindow;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class PrrucollectActivity extends BaseActivity implements OnRobotListener {
    private ImageMap1 map;
    private int mapHeight;
    private boolean isStart = false;
    private boolean isPrruCollect = false;
    private boolean isAutoFind = false;
    private PrruModel nowCollectPrru;
    private int coorCount = 0;
    private Path path;
    private LineShape lineShape;
    private float lastX, lastY, nowX, nowY;
    private float[] newF,desF,mXY,rXY;
    private String currentMap;
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

    private ImageView iv_collect;
    private TextView tv_forcestop,tv_useridsetting,popup_confirm,popup_cancel;
    private MyListView lv_userid;
    private UserIdAdapter userIdAdapter;
    private List<String> userIdList=new ArrayList<>();
    private List<String> ipList=new ArrayList<>();

    private LinearLayout ll_addshow,ll_add;
    private RelativeLayout rl_add,rl_yes,rl_no;
    private EditText et_userid;
private String userIds;

private List<PointF> testLocList=new LinkedList<>();
private boolean isTestLine=false;
//    private boolean showBattery=true;
//    private LinearLayout ll_battery;
//    private ImageView iv_battery;
//    private TextView tv_battery;
//    private int nowPercentPic=-1;

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


//    private List<PrruModel> mPrruModelList;
    private TextView tv_home_back;
    private TextView mSwitch;
    private RockerView mRockerView;


    //防止多个请求同时响应产生的线程不安全问题
    private synchronized void recordMaxRsrp(Float rsrp, float logX, float logY) {
        if (rsrp != null && rsrp - maxRsrp >= 0) {
            xWhenMax = logX;
            yWhenMax = logY;
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
        mContext = PrrucollectActivity.this;
        setContentView(R.layout.activity_prrucollect);
    }


    @Override
    public void dealLogicBeforeInitView() {
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        mSwitch = findViewById(R.id.tv_switch);
        iv_collect=findViewById(R.id.iv_collect);
        tv_forcestop=findViewById(R.id.tv_forcestop);
        tv_useridsetting=findViewById(R.id.tv_useridsetting);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        mSwitch.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        tv_forcestop.setOnClickListener(this);
        tv_useridsetting.setOnClickListener(this);
//        ll_battery=findViewById(R.id.ll_battery);
//        iv_battery=findViewById(R.id.iv_battery);
//        tv_battery=findViewById(R.id.tv_battery);
    }



    @Override
    public void dealLogicAfterInitView() {
        currentMap = getIntent().getExtras().getString("currentMap");
        ro = new RobotOperation(Constant.robotIp, Constant.robotPort, currentMap,this,this);
        ro.setNotify(true);
        ro.startOperation();
        initRocker();

        userIds = SharedPrefHelper.getString(this, "userId", "");//临时取出赋值给UserId
        if(!TextUtils.isEmpty(userIds)){
            for(String str:userIds.split(";")){
                ipList.add(str);
            }
        }
    }

    private void initShape() {
        cv = new CompassView(PrrucollectActivity.this);
        cv.setId(0);
        cv.setImageResource(R.mipmap.icon_robot);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape = new RequestShape("s", -16776961, cv, PrrucollectActivity.this);
        desShape = new CustomShape("des", R.color.blue, PrrucollectActivity.this, "dwf", R.mipmap.destination_point);
    }

    private void initMap() {
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
                    rXY = mapToReal(point.x, point.y);
                    ro.cancelAndMoveTo(rXY[0], rXY[1]);
                    map.setCanChange(false);
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

    private float[] mapToReal(float mx, float my) {
        return new float[]{mx  / Constant.mapScale , (mapHeight-my) /Constant.mapScale };
    }

    private float[] realToMap(float rx, float ry) {
        return new float[]{rx  * Constant.mapScale , mapHeight-ry*Constant.mapScale};
    }


    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_home_back:
                testLocList.add(new PointF(5,62));
                testLocList.add(new PointF(6,62));
                testLocList.add(new PointF(7,62));
                startTestLine(testLocList);
                break;
            case R.id.tv_switch: //切换操作模式
//                ro.forceStop();
//                if (mSwitchAutoFlag == true)//切换为手动
//                {
//                    mSwitchAutoFlag = false;
//                    iv_operation.setVisibility(View.GONE);
//                    mRockerView.setVisibility(View.VISIBLE);
//                    mSwitch.setText("手动");
//                } else {                 //切换为自动
//                    mSwitchAutoFlag = true;
//                    iv_operation.setVisibility(View.VISIBLE);
//                    mRockerView.setVisibility(View.GONE);
//                    mSwitch.setText("自动");
//                }
                break;
            case R.id.iv_operation:
//                if (isAutoFind) {
//                    ro.forceStop();
//                    isAutoFind = false;
//                    iv_operation.setImageResource(R.mipmap.home_start);
//                    return;
//                }else{
//                    if(nowCollectPrru==null){
//                        showToast("请先选择Prru");
//                        return;
//                    }
//                    desF=realToMap(nowCollectPrru.x,nowCollectPrru.y);
//                    desShape.setValues(desF[0], desF[1]);
//                    map.addShape(desShape, false);
//                    isAutoFind = true;
//                    iv_operation.setImageResource(R.mipmap.home_stop);
//                    nowCollectNeCode = nowCollectPrru.neCode;
//                    maxRsrp = Float.NEGATIVE_INFINITY;
//                    xWhenMax = Float.NEGATIVE_INFINITY;
//                    yWhenMax = Float.NEGATIVE_INFINITY;
//                    if (nowCollectPrru.x  == nowX && nowCollectPrru.y == nowY) {
//                        cStep = 1;
//                        LLog.getLog().e("扫描", "1");
//                        ro.moveTo(nowCollectPrru.x  + 0.5f, nowCollectPrru.y );
//                    } else {
//                        cStep = 0;
//                        ro.moveTo(nowCollectPrru.x, nowCollectPrru.y );
//                    }
//                }
                break;
            case R.id.tv_useridsetting:
                if(isPrruCollect){
                    showToast("请先关闭采集");
                    return;
                }
                initUserIdPop();
                break;
            case  R.id.iv_collect:
                if(isPrruCollect){
                    isPrruCollect=false;
                    iv_collect.setImageResource(R.mipmap.iv_collectoff);
                }else{
                    if(ipList.size()==0){
                        showToast("至少配置一个userId");
                        return;
                    }
                    isPrruCollect=true;
                    Glide.with(this).load(R.mipmap.iv_collecton_gif).into(iv_collect);
//                    iv_collect.setImageResource(R.mipmap.iv_collecton_gif);
//                    Vector vector = new Vector();
//                    Location location;
//                    float x = 10.5f;
//                    float y = 23.5f;
//                    boolean flag = true;
//                    for(int i = 0; i < 20; i++){
//                        location = new Location();
//                        location.setX(x);
//                        location.setY(y);
//                        location.setZ(0.0f);
//                        if(i%2 == 0){
//                            y += 0.5f;
//                        }else {
//                            if(flag){
//                                x += 39.0f;
//                                flag = false;
//                            }else {
//                                x -= 39.0f;
//                                flag = true;
//                            }
//
//                        }
//                        vector.add(location);
//                    }
//                    ro.moveToVector(vector);
                }
                break;
            case  R.id.tv_forcestop:
                ro.forceStop();
                break;
            case R.id.popup_confirm:
                String newUserId=et_userid.getText().toString();
                if(canUserIdAdd(newUserId)){
                    userIdList.add(newUserId);
                }
                ipList.clear();
                if(userIdList.size()>0) {
                    StringBuffer sb=new StringBuffer();
                    for (String str : userIdList) {
                        ipList.add(str);
                        sb.append(";"+str);
                    }
                    SharedPrefHelper.putString(this,"userId",sb.substring(1));
                }else {
                    SharedPrefHelper.putString(this,"userId","");
                }
                resetUserIdAdd();
                hideUserIdPop();
                break;
            case R.id.popup_cancel:
                resetUserIdAdd();
                hideUserIdPop();
                break;
            case R.id.rl_add:
                rl_add.setVisibility(View.GONE);
                ll_add.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_yes:
                String newUserId1=et_userid.getText().toString().trim();
                if(canUserIdAdd(newUserId1)) {
                    userIdList.add(newUserId1);
                    refreshUserIdList(userIdList);
                }else{
                    showToast("空白或重复添加");
                }
                break;
            case R.id.rl_no:
                resetUserIdAdd();
                break;

            default:
                break;
        }
    }

    private boolean canUserIdAdd(String newUserId){
        if(newUserId.equals("")){
            return false;
        }
        for(String str:userIdList){
            if(str.equals(newUserId)){
                return false;
            }
        }
        return true;
    }

private void resetUserIdAdd(){
        et_userid.setText("");
    rl_add.setVisibility(View.VISIBLE);
    ll_add.setVisibility(View.GONE);


}

    private void refreshUserIdList(List<String> userIdList){
        resetUserIdAdd();
        if(userIdList.size()<10){
            ll_addshow.setVisibility(View.VISIBLE);
        }else{
            ll_addshow.setVisibility(View.GONE);
        }
        userIdAdapter.setUserIdList(userIdList);
        userIdAdapter.notifyDataSetChanged();
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
    private void initUserIdPop() {
        userIdList.clear();
            for(String str:ipList){
                userIdList.add(str);
            }
        if(mSuperPopupWindow==null) {
            mSuperPopupWindow = new SuperPopupWindow(mContext, R.layout.popup_userid);
            mSuperPopupWindow.setFocusable(true);
            mSuperPopupWindow.setOutsideTouchable(true);
            mSuperPopupWindow.setAnimotion(R.style.PopAnimation);
            popupView = mSuperPopupWindow.getPopupView();
            popup_confirm = popupView.findViewById(R.id.popup_confirm);
            popup_cancel = popupView.findViewById(R.id.popup_cancel);
            lv_userid=popupView.findViewById(R.id.lv_userid);
            userIdAdapter=new UserIdAdapter(this,userIdList);
            lv_userid.setAdapter(userIdAdapter);
            popup_confirm.setOnClickListener(this);
            popup_cancel.setOnClickListener(this);
            ll_addshow = popupView.findViewById(R.id.ll_addshow);
            ll_add = popupView.findViewById(R.id.ll_add);
            et_userid = popupView.findViewById(R.id.et_userid);
            rl_add = popupView.findViewById(R.id.rl_add);
            rl_no = popupView.findViewById(R.id.rl_no);
            rl_yes = popupView.findViewById(R.id.rl_yes);
            rl_yes.setOnClickListener(this);
            rl_no.setOnClickListener(this);
            rl_add.setOnClickListener(this);
            userIdAdapter.setOnUserIdListener(new UserIdAdapter.OnUserIdListener() {
                @Override
                public void delete(int position) {
                    userIdList.remove(position);
                    refreshUserIdList(userIdList);
                }
            });
        }
        showUserIdPop();
    }

    /**
     * 显示Popupwindow
     */
    private void showUserIdPop() {
        mSuperPopupWindow.showPopupWindow();
    }

    /**
     * 隐藏Popupwindow
     */
    private void hideUserIdPop() {
        mSuperPopupWindow.hidePopupWindow();
    }




    /***
     *
     * @param x
     * @param y
     * @param scaleRuler
     */
    private void saveRbLocationInfo(float x, float y, float scaleRuler) {
        SharedPrefHelper.putString(PrrucollectActivity.this, "currentMap", currentMap);
        mXY=realToMap(x,y);
        initStart(mXY[0], mXY[1]);
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
//        // 设置回调模式
//        mRockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
//        //监听摇动方向
//        mRockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
//            @Override
//            public void onStart() {
//                //开始的方法
////                RobotMoveUtils.goStraight(0);
//            }
//
//            @Override
//            public void angle(double v) {
//                //角度
////               RobotMoveUtils.setRobotMove(platform,RobotMoveUtils.fuzzyDirection(v), (HomeActivity) mContext);
//            }
//
//            @Override
//            public void onFinish() {
//                RobotMoveUtils.goStraight(0);
//                //停止
//            }
//        });
//        mRockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
//            @Override
//            public void onStart() {
//                Log.e("XHF", "Start");
//                //开始循环监听状态值
////                mMoveT？
//            }
//
//            @Override
//            public void direction(RockerView.Direction direction) {
////                handRobotMove(direction);
//                Log.e("XHF", "摇动方向 : " + getDirection(direction));
//            }
//
//            @Override
//            public void onFinish() {
//                Log.e("XHF", "Finish");
////                mMoveTimer.cancel();
//            }
//        });
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
                ro.moveTo(nowCollectPrru.x  + 0.5f, nowCollectPrru.y );
                break;
            case 2:
                LLog.getLog().e("扫描", "2");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y );
                break;
            case 3:
                LLog.getLog().e("扫描", "3");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y  + 0.5f);
                break;
            case 4:
                LLog.getLog().e("扫描", "4");
                ro.moveTo(nowCollectPrru.x , nowCollectPrru.y );
                break;
            case 5:
                LLog.getLog().e("扫描", "5");
                ro.moveTo(nowCollectPrru.x  - 0.5f, nowCollectPrru.y );
                break;
            case 6:
                LLog.getLog().e("扫描", "6");
                ro.moveTo(nowCollectPrru.x , nowCollectPrru.y );
                break;
            case 7:
                LLog.getLog().e("扫描", "7");
                ro.moveTo(nowCollectPrru.x , nowCollectPrru.y  - 0.5f);
                break;
            case 8:
                LLog.getLog().e("扫描", "8");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y );
                break;
            case 9:
                LLog.getLog().e("扫描", "9");
                isAutoFind = false;
                arriveDes();
                iv_operation.setImageResource(R.mipmap.home_start);
                if (maxRsrp > -10000f) {
                    xyRobotWhenMax = realToMap(xWhenMax, yWhenMax);
                    CollectPointShape maxRsrpPointShape = new CollectPointShape(nowCollectPrru.neCode, R.color.route_color, PrrucollectActivity.this, "dwf");
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
        map.setCanChange(true);
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
        mXY = realToMap(nowX, nowY);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape.setView(cv);
        robotShape.setValues(mXY[0], mXY[1]);
    }

    @Override
    public void connectSuccess(float x, float y,  float direc,boolean isContinue) {
        showToast("机器人连接成功！");
        initMap();

        nowX = x;
        nowY = y;
        robotDirection = direc;
        if (isContinue) {
            float[] continueXY = realToMap(x, y);
            initStart(continueXY[0], continueXY[1]);
        } else {
            saveRbLocationInfo(x, y, Constant.mapScale);
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
        LLog.getLog().e("异常断开", errormsg);
        //finish();
    }

    @Override
    public void refreshOrbits(Vector<Location> locVector) {
        clearOrbits();
        coorCount = locVector.size();
        for (int i = 0, len = coorCount; i < len; i++) {
            float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
            CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, PrrucollectActivity.this, "dwf", R.mipmap.orbit_point);
            orbitShape.setValues(tf[0], tf[1]);
            map.addShape(orbitShape, false);
        }
    }


    @Override
    public void notifyPrru(float x, float y) {
        if(!isPrruCollect){
            return;
        }
        final float logX = x ;
        final float logY = y ;
        for(final String ip: ipList){
            Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + ip + "&mapId=1", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    LLog.getLog().e("getLocAndPrruInfo成功", s);
                    LocAndPrruInfoResponse lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                    if (lap.code == 0 && lap.data.prruData != null) {
                        LLog.getLog().prru(logX + "," + logY, prruDataToString(lap.data.prruData), ip);
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
        if(!isForce) {
            if(isTestLine){
                continueTestLine();
            }
            if(isAutoFind) {
                autoFind();
            }
        }else{
            arriveDes();
        }
    }

    private void startTestLine(List<PointF> locList){
        if(locList!=null&&locList.size()>0){
            testLocList=locList;
            isTestLine=true;
            showToast("路径测试开始");
            ro.moveTo(testLocList.get(0).x,testLocList.get(0).y);
        }else{
            showToast("路径为空");
        }
    }

    private void continueTestLine(){
        testLocList.remove(0);
        if(testLocList.size()>0){
            ro.moveTo(testLocList.get(0).x,testLocList.get(0).y);
        }else{
            isTestLine=false;
            showToast("路径测试完成");
        }
    }

}
