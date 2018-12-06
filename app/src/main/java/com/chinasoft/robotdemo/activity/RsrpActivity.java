package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.RouteAdapter;
import com.chinasoft.robotdemo.adapter.UserIdAdapter;
import com.chinasoft.robotdemo.bean.LocAndPrruInfoResponse;
import com.chinasoft.robotdemo.bean.MaxrsrpPosition;
import com.chinasoft.robotdemo.bean.PrruData;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.bean.PrruSigalModel;
import com.chinasoft.robotdemo.bean.RouteModel;
import com.chinasoft.robotdemo.db.dbflow.DirectionData;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.robot.OnRobotListener;
import com.chinasoft.robotdemo.robot.RobotOperation;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.DBUtils;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.view.CompassView;
import com.chinasoft.robotdemo.view.MyListView;
import com.chinasoft.robotdemo.view.popup.SuperPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongqw.rockerlibrary.view.RockerView;
import com.slamtec.slamware.robot.Location;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CircleShape;
import net.yoojia.imagemap.core.CollectPointShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.PrruGkcShape;
import net.yoojia.imagemap.core.RequestShape;
import net.yoojia.imagemap.core.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class RsrpActivity extends BaseActivity implements OnRobotListener {
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
    private float[] newF, desF, mXY, rXY;
    private String currentMap;
    private ImageView iv_operation;
    private RequestShape robotShape;
    private CompassView cv;
    private int cStep;
    private String nowCollectNeCode;
    private float maxRsrp, xWhenMax, yWhenMax;
    private float[] xyRobotWhenMax;
    private CustomShape desShape;
    private SuperPopupWindow mUseridPopupWindow, mRoutePopupWindow;
    private Context mContext;
    private View popupView_userid, popupView_route;
    private float mapRotate;
    private float robotDirection;
    private RobotOperation ro;
    private boolean mSwitchAutoFlag = true; //默认为自动

    private ImageView iv_collect;
    private TextView tv_forcestop, tv_useridsetting, popup_confirm_userid, popup_cancel_userid;
    private TextView tv_routesetting, popup_confirm_route, popup_cancel_route;
    private MyListView lv_userid, lv_route;
    private UserIdAdapter userIdAdapter;
    private RouteAdapter routeAdapter;
    private List<String> userIdList = new ArrayList<>();
    private List<String> ipList = new ArrayList<>();

    private List<DirectionData> routeList = new ArrayList<>();

    private LinearLayout ll_addshow_userid, ll_add_userid;
    private LinearLayout ll_addshow_route, ll_add_route;
    private RelativeLayout rl_add_userid, rl_yes_userid, rl_no_userid;
    private RelativeLayout rl_add_route, rl_yes_route, rl_no_route;
    private TextView tv_pop_route_title;
    private String newRouteJson;
    private String nowRouteName;
    private List<PointF> nowRouteList = new ArrayList<>();
    private DirectionData selectRouteModel;

    private EditText et_userid;
    private EditText et_route;
    private String userIds;

    private boolean isTestLine = false;
    private int locCount;
    private LineShape routeLineShape;
    private Path routeLinePath;
    private Map<String, MaxrsrpPosition> mpMap = new HashMap<>();

    private float rX;
    private float rY;

    private TextView tv_home_back;
    private TextView tv_opeleft;
    private int mode_opeleft=0;//操作左边按钮的状态，0为隐藏，1为恢复，2为清除
    private SuperPopupWindow mChooseCenterPointPop;
    private String mMapName;


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
        mContext = RsrpActivity.this;
        setContentView(R.layout.activity_rsrp);
    }


    @Override
    public void dealLogicBeforeInitView() {
        routeLinePath = new Path();
        routeLineShape = new LineShape("routeLine", R.color.green, 2, "#FF4081");

    }


    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        tv_opeleft = findViewById(R.id.tv_opeleft);
        iv_collect = findViewById(R.id.iv_collect);
        tv_forcestop = findViewById(R.id.tv_forcestop);
        tv_useridsetting = findViewById(R.id.tv_useridsetting);
        tv_routesetting = findViewById(R.id.tv_routesetting);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        tv_opeleft.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        tv_forcestop.setOnClickListener(this);
        tv_useridsetting.setOnClickListener(this);
        tv_routesetting.setOnClickListener(this);
    }


    @Override
    public void dealLogicAfterInitView() {
        currentMap = getIntent().getExtras().getString("currentMap");
        mMapName = currentMap;
//        ro = new RobotOperation(Constant.robotIp, Constant.robotPort, currentMap, this, this,2000);
//        ro.setNotify(true);
//        ro.startOperation();

        userIds = SharedPrefHelper.getString(this, "userId", "");//临时取出赋值给UserId
        if (!TextUtils.isEmpty(userIds)) {
            for (String str : userIds.split(";")) {
                ipList.add(str);
            }
        }
        map.setMapBitmap(Constant.mapBitmap);
        mapHeight = Constant.mapBitmap.getHeight();

        if (ro != null && !ro.getContinue()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initCenterPop();
                                mChooseCenterPointPop.showPopupWindow();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    private void initShape() {
        cv = new CompassView(RsrpActivity.this);
        cv.setId(0);
        cv.setImageResource(R.mipmap.icon_robot);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape = new RequestShape("s", -16776961, cv, RsrpActivity.this);
        desShape = new CustomShape("des", R.color.blue, RsrpActivity.this, "dwf", R.mipmap.destination_point);
    }

    private void initMap() {
        map.setMapBitmap(Constant.mapBitmap);
        mapHeight = Constant.mapBitmap.getHeight();
        initShape();
//        map.setOnRotateListener(new TouchImageView1.OnRotateListener() {
//            @Override
//            public void onRotate(float rotate) {
//                mapRotate = -rotate;
//                cv.updateDirection(mapRotate + robotDirection);
//                robotShape.setView(cv);
//            }
//        });
//        map.setOnLongClickListener1(new TouchImageView1.OnLongClickListener1() {
//            @Override
//            public void onLongClick(PointF point) {
//                if (isStart && !isAutoFind) {
//                    rXY = mapToReal(point.x, point.y);
//                    ro.cancelAndMoveTo(rXY[0], rXY[1]);
//                    map.setCanChange(false);
//                    desShape.setValues(point.x, point.y);
//                    map.addShape(desShape, false);
//                }
//            }
//        });
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

    private float[] mapToReal(float mx, float my, int height) {
        return new float[]{mx / Constant.mapScale, (height - my) / Constant.mapScale};
    }

    private float[] mapToReal(float mx, float my) {
        return new float[]{mx / Constant.mapScale, (mapHeight - my) / Constant.mapScale};
    }

    private float[] realToMap(float rx, float ry) {
        return new float[]{rx * Constant.mapScale, mapHeight - ry * Constant.mapScale};
    }


    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_home_back:
                finish();
                break;
            case R.id.tv_opeleft: //操作左边的按钮
                if(mode_opeleft==1){
                    mode_opeleft=0;
                    tv_opeleft.setVisibility(View.GONE);
                    startTestLine();
                }else if(mode_opeleft==2){
                    mode_opeleft=0;
                    tv_opeleft.setVisibility(View.GONE);
                    clearLastTest();
                }
                break;
            case R.id.iv_operation:
                if(isTestLine){
                    isTestLine = false;
                    iv_operation.setImageResource(R.mipmap.home_start);
                    drawPrruAfterTestLine();
                    showToast("路径测试结束");
                    showClear();
                    try {
                        ro.forceStop();
                    }catch (Exception e){
                        showToast("机器人异常："+e.toString());
                    }
                }else{
                    if(mode_opeleft==2){
                        showToast("请先清除痕迹并重选路径");
                        return;
                    }
                    if (ipList.size() == 0) {
                        showToast("用第一个userId测试，未配置");
                        return;
                    }
                    if (nowRouteList.size() == 0) {
                        showToast("未选择路径");
                        return;
                    }
                    iv_operation.setImageResource(R.mipmap.home_stop);
                    locCount=nowRouteList.size();
                    isTestLine = true;
                    drawLineBeforeTestLine();
                    mpMap.clear();
                    startTestLine();
                }
                break;
            case R.id.tv_useridsetting:
                if (isTestLine) {
                    showToast("请先关闭或完成路径测试");
                    return;
                }
                if (isPrruCollect) {
                    showToast("请先关闭采集");
                    return;
                }
                initUserIdPop();
                break;
            case R.id.tv_routesetting:
                if(mode_opeleft==2){
                    showToast("请先清除痕迹");
                    return;
                }
                if (isTestLine) {
                    showToast("请先关闭或完成路径测试");
                    return;
                }
                if (isPrruCollect) {
                    showToast("请先关闭采集");
                    return;
                }
                initRoutePop();
                break;
            case R.id.iv_collect:
                if (isPrruCollect) {
                    isPrruCollect = false;
                    iv_collect.setImageResource(R.mipmap.iv_collectoff);
                } else {
                    if (ipList.size() == 0) {
                        showToast("至少配置一个userId");
                        return;
                    }
                    isPrruCollect = true;
                    Glide.with(this).load(R.mipmap.iv_collecton_gif).into(iv_collect);
                }
                break;
            case R.id.tv_forcestop:
                ro.forceStop();
                if(isTestLine){
                    showRegain();
                }
                break;
            case R.id.popup_confirm_userid:
                String newUserId = et_userid.getText().toString().trim();
                if (canUserIdAdd(newUserId)) {
                    userIdList.add(newUserId);
                }
                ipList.clear();
                if (userIdList.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (String str : userIdList) {
                        ipList.add(str);
                        sb.append(";" + str);
                    }
                    SharedPrefHelper.putString(this, "userId", sb.substring(1));
                } else {
                    SharedPrefHelper.putString(this, "userId", "");
                }
                resetUserIdAdd();
                hideUserIdPop();
                break;
            case R.id.popup_cancel_userid:
                resetUserIdAdd();
                hideUserIdPop();
                break;
            case R.id.rl_add_userid:
                rl_add_userid.setVisibility(View.GONE);
                ll_add_userid.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_yes_userid:
                String newUserId1 = et_userid.getText().toString().trim();
                if (canUserIdAdd(newUserId1)) {
                    userIdList.add(newUserId1);
                    refreshUserIdList(userIdList);
                } else {
                    showToast("空白或重复添加");
                }
                break;
            case R.id.rl_no_userid:
                resetUserIdAdd();
                break;
            case R.id.popup_confirm_route:
                if (selectRouteModel == null) {
                    showToast("未选择路径");
                    return;
                }
//                String newRouteName1 = et_route.getText().toString().trim();
//                if (canRouteAdd(newRouteName1)) {
//                    RouteModel rm=new RouteModel(newRouteName1,newRouteJson);
//                    routeList.add(rm);
//                }
                nowRouteName = selectRouteModel.getRouteName();
                removeRoute(nowRouteList.size());
                nowRouteList = new Gson().fromJson(selectRouteModel.getPath(), new TypeToken<List<PointF>>() {
                }.getType());
                drawRoute(nowRouteList);

                resetRouteAdd();
                hideRoutePop();
                break;
            case R.id.popup_cancel_route:
                resetRouteAdd();
                hideRoutePop();
                break;
            case R.id.rl_add_route:
                openActivityForResult(RouteActivity.class, 1);
                break;
            case R.id.rl_yes_route:
                String newRouteName = et_route.getText().toString().trim();
                if (canRouteAdd(newRouteName)) {
//                    userIdList.add(newRoute1);
//                    refreshRouteList(userIdList);

                    DirectionData rm = new DirectionData(mMapName, newRouteName, newRouteJson);

                    // 插入数据并更新routeList
                    DBUtils.insert(rm);
                    routeList.add(rm);
                    resetRouteAdd();
                    refreshRouteList(routeList);
                } else {
                    showToast("空白或重复添加");
                }
                break;
            case R.id.rl_no_route:
                resetRouteAdd();
                break;
            default:
                break;
        }
    }

    //清除上次测试痕迹
    private void clearLastTest(){
        removeRoute(locCount);
        for(String gpp:mpMap.keySet()){
            map.removeShape(gpp);
        }
    }

    private boolean canUserIdAdd(String newUserId) {
        if (newUserId.equals("")) {
            return false;
        }
        for (String str : userIdList) {
            if (str.equals(newUserId)) {
                return false;
            }
        }
        return true;
    }

    private boolean canRouteAdd(String routeName) {
        if (routeName.equals("")) {
            return false;
        }
        for (DirectionData route : routeList) {
            if (routeName.equals(route.getRouteName())) {
                return false;
            }
        }
        return true;
    }

    private void resetUserIdAdd() {
        et_userid.setText("");
        rl_add_userid.setVisibility(View.VISIBLE);
        ll_add_userid.setVisibility(View.GONE);
    }

    private void resetRouteAdd() {
        et_route.setText("");
        rl_add_route.setVisibility(View.VISIBLE);
        ll_add_route.setVisibility(View.GONE);
    }

    private void refreshUserIdList(List<String> userIdList) {
        resetUserIdAdd();
        if (userIdList.size() < 10) {
            ll_addshow_userid.setVisibility(View.VISIBLE);
        } else {
            ll_addshow_userid.setVisibility(View.GONE);
        }
        userIdAdapter.setUserIdList(userIdList);
        userIdAdapter.notifyDataSetChanged();
    }

    private void refreshRouteList(List<DirectionData> routeList) {
        resetRouteAdd();
        routeAdapter.setRouteList(routeList);
        routeAdapter.notifyDataSetChanged();
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
        for (String str : ipList) {
            userIdList.add(str);
        }
        if (mUseridPopupWindow == null) {
            mUseridPopupWindow = new SuperPopupWindow(mContext, R.layout.popup_userid);
            mUseridPopupWindow.setFocusable(true);
            mUseridPopupWindow.setOutsideTouchable(true);
            mUseridPopupWindow.setAnimotion(R.style.PopAnimation);
            popupView_userid = mUseridPopupWindow.getPopupView();
            popup_confirm_userid = popupView_userid.findViewById(R.id.popup_confirm_userid);
            popup_cancel_userid = popupView_userid.findViewById(R.id.popup_cancel_userid);
            lv_userid = popupView_userid.findViewById(R.id.lv_userid);
            userIdAdapter = new UserIdAdapter(this, userIdList);
            lv_userid.setAdapter(userIdAdapter);
            popup_confirm_userid.setOnClickListener(this);
            popup_cancel_userid.setOnClickListener(this);
            ll_addshow_userid = popupView_userid.findViewById(R.id.ll_addshow_userid);
            ll_add_userid = popupView_userid.findViewById(R.id.ll_add_userid);
            et_userid = popupView_userid.findViewById(R.id.et_userid);
            rl_add_userid = popupView_userid.findViewById(R.id.rl_add_userid);
            rl_no_userid = popupView_userid.findViewById(R.id.rl_no_userid);
            rl_yes_userid = popupView_userid.findViewById(R.id.rl_yes_userid);
            rl_yes_userid.setOnClickListener(this);
            rl_no_userid.setOnClickListener(this);
            rl_add_userid.setOnClickListener(this);
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
        mUseridPopupWindow.showPopupWindow();
    }

    /**
     * 隐藏Popupwindow
     */
    private void hideUserIdPop() {
        mUseridPopupWindow.hidePopupWindow();
    }

    /***
     * 初始化开始popupwindow
     */
    private void initRoutePop() {
        //   查询数据库，赋予routeList；
        routeList = DBUtils.query(mMapName);

        if (mRoutePopupWindow == null) {
            mRoutePopupWindow = new SuperPopupWindow(mContext, R.layout.popup_route);
            mRoutePopupWindow.setFocusable(true);
            mRoutePopupWindow.setOutsideTouchable(true);
            mRoutePopupWindow.setAnimotion(R.style.PopAnimation);
            popupView_route = mRoutePopupWindow.getPopupView();
            popup_confirm_route = popupView_route.findViewById(R.id.popup_confirm_route);
            popup_cancel_route = popupView_route.findViewById(R.id.popup_cancel_route);
            lv_route = popupView_route.findViewById(R.id.lv_route);
            routeAdapter = new RouteAdapter(this, routeList);
            lv_route.setAdapter(routeAdapter);
            popup_confirm_route.setOnClickListener(this);
            popup_cancel_route.setOnClickListener(this);
            ll_addshow_route = popupView_route.findViewById(R.id.ll_addshow_route);
            ll_add_route = popupView_route.findViewById(R.id.ll_add_route);
            et_route = popupView_route.findViewById(R.id.et_route);
            rl_add_route = popupView_route.findViewById(R.id.rl_add_route);
            rl_no_route = popupView_route.findViewById(R.id.rl_no_route);
            rl_yes_route = popupView_route.findViewById(R.id.rl_yes_route);
            tv_pop_route_title = popupView_route.findViewById(R.id.tv_pop_route_title);
            rl_yes_route.setOnClickListener(this);
            rl_no_route.setOnClickListener(this);
            rl_add_route.setOnClickListener(this);
            routeAdapter.setOnRouteListener(new RouteAdapter.OnRouteListener() {
                @Override
                public void delete(DirectionData routeModel) {
                    if (routeModel == selectRouteModel) {
                        showToast("不能删除选择的路径");
                        return;
                    }
                    //  删除数据库并更新routeList
                    DBUtils.delete(routeModel);//删除
                    routeList.remove(routeModel);
                    refreshRouteList(routeList);
                }

                @Override
                public void select(DirectionData routeModel) {
                    if (routeModel != selectRouteModel) {
                        selectRouteModel = routeModel;
                        tv_pop_route_title.setText(selectRouteModel.getRouteName());
                    }
                }
            });
        }
        showRoutePop();
    }

    /**
     * 显示Popupwindow
     */
    private void showRoutePop() {
        if (!TextUtils.isEmpty(nowRouteName)) {
            tv_pop_route_title.setText(nowRouteName);
            selectRouteModel = getRouteModelByRouteName(nowRouteName);
        }
        mRoutePopupWindow.showPopupWindow();
    }

    private DirectionData getRouteModelByRouteName(String routeName) {
        for (DirectionData route : routeList) {
            if (routeName.equals(route.getRouteName())) {
                return route;
            }
        }
        return null;
    }

    /**
     * 隐藏Popupwindow
     */
    private void hideRoutePop() {
        tv_pop_route_title.setText("");
        selectRouteModel = null;
        mRoutePopupWindow.hidePopupWindow();
    }


    /***
     *
     * @param x
     * @param y
     * @param scaleRuler
     */
    private void saveRbLocationInfo(float x, float y, float scaleRuler) {
        SharedPrefHelper.putString(RsrpActivity.this, "currentMap", currentMap);
        mXY = realToMap(x, y);
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



    private long lastClickTime;

    @Override
    public void onBackPressed() {
        long nowClickTime = System.currentTimeMillis();
        if (nowClickTime - lastClickTime > 2000) {
            lastClickTime = nowClickTime;
            showToast("再点一次回退");
        } else {
            finish();
        }
    }

    private void autoFind() {
        cStep++;
        switch (cStep) {
            case 1:
                LLog.getLog().e("扫描", "1");
                ro.moveTo(nowCollectPrru.x + 0.5f, nowCollectPrru.y);
                break;
            case 2:
                LLog.getLog().e("扫描", "2");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y);
                break;
            case 3:
                LLog.getLog().e("扫描", "3");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y + 0.5f);
                break;
            case 4:
                LLog.getLog().e("扫描", "4");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y);
                break;
            case 5:
                LLog.getLog().e("扫描", "5");
                ro.moveTo(nowCollectPrru.x - 0.5f, nowCollectPrru.y);
                break;
            case 6:
                LLog.getLog().e("扫描", "6");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y);
                break;
            case 7:
                LLog.getLog().e("扫描", "7");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y - 0.5f);
                break;
            case 8:
                LLog.getLog().e("扫描", "8");
                ro.moveTo(nowCollectPrru.x, nowCollectPrru.y);
                break;
            case 9:
                LLog.getLog().e("扫描", "9");
                isAutoFind = false;
                arriveDes();
                iv_operation.setImageResource(R.mipmap.home_start);
                if (maxRsrp > -10000f) {
                    xyRobotWhenMax = realToMap(xWhenMax, yWhenMax);
                    CollectPointShape maxRsrpPointShape = new CollectPointShape(nowCollectPrru.neCode, R.color.route_color, RsrpActivity.this, "dwf");
                    maxRsrpPointShape.setValues(xyRobotWhenMax[0], xyRobotWhenMax[1]);
                    map.addShape(maxRsrpPointShape, false);
                }
                break;
            default:
                break;
        }
    }


    //移除轨迹点
    private void clearOrbits() {
        for (int i = 0, len = coorCount; i < len; i++) {
            map.removeShape("coor" + i);
        }
    }

    //到达目的点
    private void arriveDes() {
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
    private synchronized void updateRobotByReal() {
        mXY = realToMap(nowX, nowY);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape.setView(cv);
        robotShape.setValues(mXY[0], mXY[1]);
    }

    @Override
    public void connectSuccess(float x, float y, float direc, boolean isContinue) {
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
        if(isTestLine){
            showRegain();
        }
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
            CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, RsrpActivity.this, "dwf", R.mipmap.orbit_point);
            orbitShape.setValues(tf[0], tf[1]);
            map.addShape(orbitShape, false);
        }
    }

    private MaxrsrpPosition tempMp;

    private synchronized void recordMaxrsrpPostion(float x, float y, List<PrruSigalModel> prruSigalModelList) {
        if (prruSigalModelList == null) {
            return;
        }
        for (PrruSigalModel psm : prruSigalModelList) {
            if (psm.rsrp > -950) {
                LLog.getLog().robot(x + "," + y, psm.gpp + "____" + psm.rsrp);
                if (!mpMap.keySet().contains(psm.gpp)) {
                    MaxrsrpPosition mp = new MaxrsrpPosition();
                    mp.setX(x);
                    mp.setY(y);
                    mp.setRsrp(psm.rsrp);
                    mpMap.put(psm.gpp, mp);
                } else {
                    tempMp = mpMap.get(psm.gpp);
                    if (psm.rsrp > tempMp.getRsrp()) {
                        tempMp.setX(x);
                        tempMp.setY(y);
                        tempMp.setRsrp(psm.rsrp);
                    }
                }
            }
        }
    }

    @Override
    public void notifyPrru(final float x, final float y) {
        if (isTestLine) {
            Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + ipList.get(0) + "&mapId=1", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    LLog.getLog().e("getLocAndPrruInfo成功", s);
                    LocAndPrruInfoResponse lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                    if (lap.code == 0) {
                        recordMaxrsrpPostion(x, y, lap.data.prruData);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    LLog.getLog().e("getLocAndPrruInfo错误", volleyError.toString());
                }
            });

             Constant.interRequestUtil.getPhonePrru(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getPhonePrru?userId=" + ipList.get(0) + "&mapId=1", new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    LLog.getLog().e("getPhonePrru成功", s);
                    LLog.getLog().robot(x + "," + y, s);

                    PrruData prruData = new Gson().fromJson(s, PrruData.class);
                    if (prruData.getCode() == 0) //成功
                    {
                        PrruData.DataBean data = prruData.getData();
                        setPrruColorPoint(data.getRsrp(), data.getId());

                    } else {
                        Toast.makeText(mContext, "prru失败", Toast.LENGTH_LONG);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    LLog.getLog().e("getPhonePrru错误", volleyError.toString());
                }
            });

        } else if (isPrruCollect) {
            for (String ip : ipList) {
                Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + ip + "&mapId=1", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LLog.getLog().e("getLocAndPrruInfo成功", s);
                        LocAndPrruInfoResponse lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                        if (lap.code == 0 && lap.data.prruData != null) {
                            LLog.getLog().prru(x + "," + y, prruDataToString(lap.data.prruData), lap.data.userId);
                            if (isAutoFind) {
                                Float rsrp = getRsrpByGpp(nowCollectNeCode, lap.data.prruData);
                                recordMaxRsrp(rsrp, x, y);
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


    }

    @Override
    public void positionChange(float x, float y, float direc) {
        nowX = x;
        nowY = y;
        robotDirection = direc;
        if (!isTestLine) {
            if (Math.sqrt((nowX - lastX) * (nowX - lastX) + (nowY - lastY) * (nowY - lastY)) > Constant.lineSpace) {
//                            Log.e("handler","的点点滴滴顶顶顶顶顶顶顶顶顶大等等");
                newF = realToMap(nowX, nowY);
                path.lineTo(newF[0], newF[1]);
                lineShape.setPath(path);
                map.addShape(lineShape, false);
                lastX = nowX;
                lastY = nowY;
            }
        }
        updateRobotByReal();
    }

    @Override
    public void moveFinish(float x, float y, float direc, boolean isForce) {
        nowX = x;
        nowY = y;
        robotDirection = direc;
        clearOrbits();
        if (!isForce) {
            if (isTestLine) {
                continueTestLine();
            }
            if (isAutoFind) {
                autoFind();
            }
        } else {
            arriveDes();
        }
    }



    private void startTestLine() {
        if (nowRouteList != null && nowRouteList.size() > 0) {
            if(nowRouteList.size()==locCount) {
                map.removeShape("des");
                showToast("路径测试开始");
            }else if(nowRouteList.size()<locCount){
                showToast("路径测试恢复");
            }
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            showToast("路径为空");
        }
    }

    private void continueTestLine() {
        map.removeShape("routePoint"+(locCount-nowRouteList.size()));
        nowRouteList.remove(0);
        if (nowRouteList.size() > 0) {
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            isTestLine = false;
            iv_operation.setImageResource(R.mipmap.home_start);
            drawPrruAfterTestLine();
//            updateRobotByReal();
            showClear();
            showToast("路径测试完成");
        }
    }

    //显示清除按钮
    private void showClear(){
        mode_opeleft=2;
        tv_opeleft.setText("清除");
        tv_opeleft.setVisibility(View.VISIBLE);
    }

    //显示恢复按钮
    private void showRegain(){
        mode_opeleft=1;
        tv_opeleft.setText("恢复");
        tv_opeleft.setVisibility(View.VISIBLE);
    }


    //mpMap按rsrp从大到小排序并画出前5个
    private void drawPrruAfterTestLine() {
        List<Map.Entry<String,MaxrsrpPosition>> lists=new ArrayList<Map.Entry<String,MaxrsrpPosition>>(mpMap.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<String, MaxrsrpPosition>>() {
            @Override
            public int compare(Map.Entry<String, MaxrsrpPosition> o1, Map.Entry<String, MaxrsrpPosition> o2) {
                float r1=o1.getValue().getRsrp();
                float r2=o2.getValue().getRsrp();
                if(r1>r2){
                    return -1;
                }else if(r1<r2){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        for(Map.Entry<String, MaxrsrpPosition> entry:lists.size()>5?lists.subList(0,5):lists){
            PrruGkcShape pgShape = new PrruGkcShape(entry.getKey(), R.color.blue, RsrpActivity.this);
            pgShape.setNecodeText(entry.getKey());
            pgShape.setPaintColor(Color.parseColor("#442b87"));
            mXY = realToMap(entry.getValue().getX(), entry.getValue().getY());
            pgShape.setValues(mXY[0], mXY[1]);
            map.addShape(pgShape, false);
        }

    }

    //移除路径显示
    private void removeRoute(int pointSize) {
        for (int i = 0; i < pointSize; i++) {
            map.removeShape("routePoint" + i);
        }
        map.removeShape("routeLine");
    }

    private void drawRoute(List<PointF> routeList) {
        routeLinePath.reset();
        for (int i = 0, len = routeList.size(); i < len; i++) {
            mXY = realToMap(routeList.get(i).x, routeList.get(i).y);
            CustomShape tShape = new CustomShape("routePoint" + i, R.color.blue, RsrpActivity.this, "dwf", R.mipmap.destination_point);
            tShape.setValues(mXY[0], mXY[1]);
            map.addShape(tShape, false);
            if (i == 0) {
                routeLinePath.moveTo(mXY[0], mXY[1]);
            } else {
                routeLinePath.lineTo(mXY[0], mXY[1]);
            }

        }
        routeLineShape.setPath(routeLinePath);
        map.addShape(routeLineShape, false);
    }

    private void drawLineBeforeTestLine() {
        routeLinePath.reset();
        for (int i = 0, len = nowRouteList.size(); i < len; i++) {
            mXY = realToMap(nowRouteList.get(i).x, nowRouteList.get(i).y);
            CustomShape tShape = new CustomShape("routePoint" + i, R.color.blue, RsrpActivity.this, "dwf", R.mipmap.destination_point);
            tShape.setValues(mXY[0], mXY[1]);
            map.addShape(tShape, false);
            if (i == 0) {
                routeLinePath.moveTo(mXY[0], mXY[1]);
            } else {
                routeLinePath.lineTo(mXY[0], mXY[1]);
            }

        }
        routeLineShape.setPath(routeLinePath);
        map.addShape(routeLineShape, false);
    }

    private void initCenterPop() {
        mChooseCenterPointPop = new SuperPopupWindow(mContext, R.layout.pop_coordinate_layout);
        mChooseCenterPointPop.setChangFocusable(true);
        View popupView = mChooseCenterPointPop.getPopupView();
        final TextView tvShowPoint = popupView.findViewById(R.id.coordinate_data);
        ImageMap1 map = popupView.findViewById(R.id.coordinate_map);
        TextView cancel = popupView.findViewById(R.id.pop_coordinate_cancel);
        TextView confirm = popupView.findViewById(R.id.pop_coordinate_confirm);
        Bitmap bitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/" + currentMap);
        final int maphight = bitmap.getHeight();
        map.setMapBitmap(bitmap);
        PointF centerByImagePoint = map.getCenterByImagePoint();
        float[] float1 = mapToReal(centerByImagePoint.x, centerByImagePoint.y, maphight);
        tvShowPoint.setText(Float.parseFloat(String.format("%.2f", float1[0])) + " , " + Float.parseFloat(String.format("%.2f", float1[1])));
        rX = Float.parseFloat(String.format("%.2f", float1[0]));
        rY = Float.parseFloat(String.format("%.2f", float1[1]));
        map.setOnCenerPointListener(new TouchImageView1.OnCenterPointListener() {
            @Override
            public void onCenter(PointF pointF) {
                float[] floats = mapToReal(pointF.x, pointF.y, maphight);
                rX = Float.parseFloat(String.format("%.2f", floats[0]));
                rY = Float.parseFloat(String.format("%.2f", floats[1]));
                tvShowPoint.setText(rX + " , " + rY);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooseCenterPointPop.hidePopupWindow();
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rX >= 0 && rY >= 0) {
                    mChooseCenterPointPop.hidePopupWindow();
                    SharedPrefHelper.putFloat(mContext, "firstX", rX);
                    SharedPrefHelper.putFloat(mContext, "firstY", rY);
                    ro.doAfterConfirm(rX, rY);
                    mChooseCenterPointPop = null;
                } else {
                    showToast("选取起始点越界");
                }
            }
        });
    }

    public void showCenterPop() {
        mChooseCenterPointPop.showPopupWindow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            rl_add_route.setVisibility(View.GONE);
            ll_add_route.setVisibility(View.VISIBLE);
            newRouteJson = data.getStringExtra("routeJson");
//            et_route.setText("默认");
        }
    }

    /**
     * 设置 PrruColor
     */
    private void setPrruColorPoint(int prru, String id) {
        int color;

        if (-75 < prru && prru <= 0) {  //深绿色
            color = Color.GREEN;
        } else if (-95 < prru && prru <= -75) { //浅绿色
            color = Color.CYAN;
        } else if (-105 < prru && prru <= -95) {  //黄色
            color = Color.YELLOW;
        } else if (-120 < prru && prru <= -105) { //红色
            color = Color.RED;
        } else {
            color = Color.BLACK;
        }
        CircleShape shape = new CircleShape(id, color);
        shape.setValues(mXY[0], mXY[1]);
        map.addShape(shape, false);
    }
}
