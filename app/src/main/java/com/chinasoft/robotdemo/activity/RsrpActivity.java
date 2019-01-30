package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.RouteAdapter;
import com.chinasoft.robotdemo.bean.LocAndPrruInfoResponse;
import com.chinasoft.robotdemo.bean.MaxrsrpPosition;
import com.chinasoft.robotdemo.bean.Position;
import com.chinasoft.robotdemo.bean.PrruInfo;
import com.chinasoft.robotdemo.bean.PrruSigalModel;
import com.chinasoft.robotdemo.db.dbflow.DirectionData;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.robot.OnRobotListener;
import com.chinasoft.robotdemo.robot.RobotOperation;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.DBUtils;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.util.UpdateCommunityInfo;
import com.chinasoft.robotdemo.view.CompassView;
import com.chinasoft.robotdemo.view.MyListView;
import com.chinasoft.robotdemo.view.popup.SuperPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slamtec.slamware.robot.Location;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CircleShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.PrruGkcShape;
import net.yoojia.imagemap.core.RequestShape;
import net.yoojia.imagemap.core.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RsrpActivity extends BaseActivity implements OnRobotListener {
    private ImageMap1 map;
    private int mapHeight;
    private boolean isStart = false;
    private boolean isAutoFind = false;
    private int coorCount = 0;
    private float nowX, nowY;
    private float[] newF, desF, mXY, rXY, tempMXY,rsrpMXY;
    private String currentMap;
    private ImageView iv_operation;
    private RequestShape robotShape;
    private CompassView cv;
    private CustomShape desShape;
    private SuperPopupWindow  mRoutePopupWindow;
    private Context mContext;
    private View  popupView_route;
    private float mapRotate;
    private float robotDirection;
    private RobotOperation ro;
    private TextView tv_forcestop;
    private TextView tv_routesetting, popup_confirm_route, popup_cancel_route;
    private MyListView lv_route;
    private RouteAdapter routeAdapter;
    private List<DirectionData> routeList = new ArrayList<>();
    private LinearLayout  ll_add_route;
    private RelativeLayout rl_add_route, rl_yes_route, rl_no_route;
    private TextView tv_pop_route_title;
    private String newRouteJson;
    private String nowRouteName;
    private List<PointF> nowRouteList = new ArrayList<>();
    private DirectionData selectRouteModel;
    private EditText et_route;
    private boolean isTestLine = false;
    private boolean isPause=false; //是否在暂停状态
    private int locCount;
    private LineShape routeLineShape;
    private Path routeLinePath;
    private Map<String, MaxrsrpPosition> mpMap = new HashMap<>();
    private float rX;
    private float rY;
    private TextView tv_home_back;
    private TextView tv_opeleft;
    private int mode_opeleft = 0;//操作左边按钮的状态，0为隐藏，1为恢复，2为清除
    private SuperPopupWindow mChooseCenterPointPop;
    private String mMapName;
    private boolean connectResult = true;
    private boolean isFabMenuOpen = false;
    private ImageView mFucAcIv;
    private LocAndPrruInfoResponse lap;
    private MaxrsrpPosition tempMp;
    private long lastClickTime;
    private UpdateCommunityInfo updateCommunityInfo;
    private int rsrpCount=0;
    //TODO 定制版本逻辑，非通用
//    private List<String> prruList =new ArrayList<String>();    //存放prru位置的map
    private List<PrruInfo> prruInfos = new ArrayList<PrruInfo>();

    @Override
    public void setContentLayout() {
        mContext = RsrpActivity.this;
        setContentView(R.layout.activity_rsrp);
    }


    @Override
    public void dealLogicBeforeInitView() {

        //TODO 定制版本逻辑，非通用
//        prruList.add("0_80_1");
//        prruList.add("0_81_1");
//        prruList.add("0_82_1");
//        prruList.add("0_83_1");
//        prruList.add("0_84_1");

        updateCommunityInfo=new UpdateCommunityInfo(this, (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE),new Handler());
        updateCommunityInfo.startUpdateData();
        routeLinePath = new Path();
        routeLineShape = new LineShape("routeLine", R.color.green, 2, "#FF4081");
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        tv_opeleft = findViewById(R.id.tv_opeleft);
        tv_forcestop = findViewById(R.id.tv_forcestop);
        tv_routesetting = findViewById(R.id.tv_routesetting);
        mFucAcIv = findViewById(R.id.fuc_menu);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        tv_opeleft.setOnClickListener(this);
        tv_forcestop.setOnClickListener(this);
        tv_routesetting.setOnClickListener(this);
        mFucAcIv.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
        currentMap = getIntent().getExtras().getString("currentMap");
        mMapName = currentMap;
        ro = new RobotOperation(Constant.robotIp, Constant.robotPort, currentMap, this, this, 2000);
//        map.setMapBitmap(Constant.mapBitmap);
//        mapHeight = Constant.mapBitmap.getHeight();
        if (connectResult && ro != null && !ro.getContinue()) {
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
                if (isStart && !isAutoFind&&(!isTestLine||isPause)) {
                    rXY = mapToReal(point.x, point.y);
                    ro.cancelAndMoveTo(rXY[0], rXY[1]);
//                    map.setCanChange(false);
                    desShape.setValues(point.x, point.y);
                    map.addShape(desShape, false);
                }
            }
        });
    }


    private void initStart(float x, float y) {
        robotShape.setValues(x, y);
        mXY=new float[]{x,y};
        map.addShape(robotShape, false);
        isStart = true;
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

    private float[] realToMap(float rx, float ry, int height) {
        return new float[]{rx * Constant.mapScale, height - ry * Constant.mapScale};
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.fuc_menu:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                } else {
                    expandFabMenu();
                }
                break;
            case R.id.tv_home_back:
                finish();
                break;
            case R.id.tv_opeleft: //操作左边的按钮
                if (mode_opeleft == 1) {
                    mode_opeleft = 0;
                    tv_opeleft.setVisibility(View.GONE);
                    startTestLine();
                } else if (mode_opeleft == 2) {
                    mode_opeleft = 0;
                    tv_opeleft.setVisibility(View.GONE);
                    clearLastTest();
                }
                break;
            case R.id.iv_operation:
                if (isTestLine) {
                    isTestLine = false;
                    iv_operation.setImageResource(R.mipmap.home_start);
                    drawPrruAfterTestLine();
                    showToast("路径测试结束");
                    showClear();
                    try {
                        ro.forceStop();
                    } catch (Exception e) {
                        showToast("机器人异常：" + e.toString());
                    }
                } else {
                    if (mode_opeleft == 2) {
                        showToast("请先清除痕迹并重选路径");
                        return;
                    }
//                    if (ipList.size() == 0) {
//                        showToast("用第一个userId测试，未配置");
//                        return;
//                    }
                    if (nowRouteList.size() == 0) {
                        showToast("未选择路径");
                        return;
                    }
                    rsrpCount=0;
                    iv_operation.setImageResource(R.mipmap.home_end);
                    locCount = nowRouteList.size();
                    isTestLine = true;
//                    drawLineBeforeTestLine();
                    mpMap.clear();
                    startTestLine();
                }
                break;
            case R.id.tv_routesetting:
                if (mode_opeleft == 2) {
                    showToast("请先清除痕迹");
                    return;
                }
                if (isTestLine) {
                    showToast("请先关闭或完成路径测试");
                    return;
                }
                initRoutePop();
                break;
            case R.id.tv_forcestop:
                ro.forceStop();
                if (isTestLine) {
                    showRegain();
                }
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
    private void clearLastTest() {
        removeRoute(locCount);
        for (String gpp : mpMap.keySet()) {
            map.removeShape(gpp);
        }
//        for (String gpp : prruMap.keySet()) {
//            map.removeShape(gpp);
//        }
        for(int i=0;i<rsrpCount;i++){
            map.removeShape("rsrp"+i);
        }

        for(int i=0;i<Constant.prruNumber;i++){
            map.removeShape(i+"");
        }
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


    private void resetRouteAdd() {
        et_route.setText("");
        rl_add_route.setVisibility(View.VISIBLE);
        ll_add_route.setVisibility(View.GONE);
    }


    private void refreshRouteList(List<DirectionData> routeList) {
        resetRouteAdd();
        routeAdapter.setRouteList(routeList);
        routeAdapter.notifyDataSetChanged();
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
        tempMXY = realToMap(x, y);
        initStart(tempMXY[0], tempMXY[1]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ro != null) {
            ro.endOperation();
            ro.disconnect();
        }
    }


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



    //移除轨迹点
    private void clearOrbits() {
        for (int i = 0, len = coorCount; i < len; i++) {
            map.removeShape("coor" + i);
        }
    }

    //到达目的点
    private void arriveDes() {
//        map.setCanChange(true);
//        newF = realToMap(nowX, nowY);
//        path.lineTo(newF[0], newF[1]);
//        lineShape.setPath(path);
//        map.addShape(lineShape, false);
//        lastX = nowX;
//        lastY = nowY;
        updateRobotByReal();
        map.removeShape("des");

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
//        showToast("机器人连接成功！");
        initMap();

        nowX = x;
        nowY = y;
        robotDirection = direc;
        if (isContinue) {
            float[] continueXY = realToMap(x, y);
            initStart(continueXY[0], continueXY[1]);
        } else {
            dismissProgressDialog();
            saveRbLocationInfo(x, y, Constant.mapScale);
        }
    }

    @Override
    public void connectFailed(String errormsg) {
        showToast("机器人连接失败！");
        connectResult = false;
        finish();
    }

    @Override
    public void catchError(String errormsg) {
//        if (isTestLine) {
//            showRegain();
//        }
        showToast("异常断开");
//        LLog.getLog().e("异常断开", errormsg);
        //finish();
        if (isTestLine) {
            isTestLine = false;
            iv_operation.setImageResource(R.mipmap.home_start);
            drawPrruAfterTestLine();
            showToast("路径测试结束");
            showClear();
            try {
                ro.forceStop();
            } catch (Exception e) {
            }
        }
    }


    @Override
    public synchronized void refreshOrbits(Vector<Location> locVector) {
        clearOrbits();
        coorCount = locVector.size();
        for (int i = 0, len = coorCount; i < len; i++) {
            float[] tf = realToMap(locVector.get(i).getX(), locVector.get(i).getY());
//            CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, RsrpActivity.this, "dwf", R.mipmap.orbit_point);
            CircleShape orbitShape = new CircleShape("coor" + i, Color.parseColor("#f6ddcc"), 7f);
            orbitShape.setValues(tf[0], tf[1]);
            map.addShape(orbitShape, false);
        }
    }

    private synchronized void recordMaxrsrpPostion(float x, float y, List<PrruSigalModel> prruSigalModelList) {
        if (prruSigalModelList == null || prruSigalModelList.size() < 1) {
            return;
        }
        Collections.sort(prruSigalModelList, new Comparator<PrruSigalModel>() {
            @Override
            public int compare(PrruSigalModel p1, PrruSigalModel p2) {
                float n1 = p1.rsrp;
                float n2 = p2.rsrp;
                if (n1 > n2) {
                    return -1;
                } else if (n1 < n2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        PrruSigalModel prruSigalModel = prruSigalModelList.get(0);
        if (!mpMap.keySet().contains(prruSigalModel.gpp)) {
            MaxrsrpPosition mp = new MaxrsrpPosition();
            mp.setX(x);
            mp.setY(y);
            mp.setRsrp(prruSigalModel.rsrp);
            mpMap.put(prruSigalModel.gpp, mp);
        } else {
            tempMp = mpMap.get(prruSigalModel.gpp);
            if (prruSigalModel.rsrp > tempMp.getRsrp()) { //判断是否更新最大rsrp值及位置
                tempMp.setX(x);
                tempMp.setY(y);
                tempMp.setRsrp(prruSigalModel.rsrp);
            }
        }
    }

    @Override
    public void notifyPrru(final float x, final float y) {
        if (isTestLine) {
            Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + Constant.userId + "&mapId="+Constant.mapId, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
//                    LLog.getLog().e("getLocAndPrruInfo成功", s);
                    lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                    if (lap.code == 0) {
                        recordMaxrsrpPostion(x, y, lap.data.prruData);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
//                    LLog.getLog().e("getLocAndPrruInfo错误", volleyError.toString());
                }
            });

            /**
             * RSRP打点
             */
            try{
                setPrruColorPoint(x,y, Integer.parseInt(updateCommunityInfo.RSRP));
            }catch (Exception e){
//                LLog.getLog().e("RSRP异常",e.toString());
            }
        }
    }

    @Override
    public void positionChange(float x, float y, float direc) {
        nowX = x;
        nowY = y;
        robotDirection = direc;
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
                if(nowRouteList.size()==0){
                    continueTestLine(false);
                }else {
                    //判断是否进入设立的点
                    if(Math.sqrt(Math.pow((nowRouteList.get(0).x-x),2)+Math.pow((nowRouteList.get(0).y-y),2))>0.5f) {
                        continueTestLine(false);
                    }else {
                        continueTestLine(true);
                    }
                }
            }
        } else {
            arriveDes();
        }
    }


    private void startTestLine() {
        isPause=false;
        clearOrbits();
        map.removeShape("des");
        if (nowRouteList != null && nowRouteList.size() > 0) {
            if (nowRouteList.size() == locCount) {
                showToast("路径测试开始");
            } else if (nowRouteList.size() < locCount) {
                showToast("路径测试恢复");
            }
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            showToast("路径为空");
        }
    }

    private void continueTestLine(boolean isRemove) {
        if(isRemove) {
            tempMXY = realToMap(nowRouteList.get(0).x, nowRouteList.get(0).y);
            CustomShape tShape = new CustomShape("routePoint" + (locCount - nowRouteList.size()), R.color.blue, RsrpActivity.this, "dwf", R.mipmap.destination_point_gray);
            tShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(tShape, false);
            nowRouteList.remove(0);
        }
        if (nowRouteList.size() > 0) {
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            iv_operation.setImageResource(R.mipmap.home_start);
            drawPrruAfterTestLine();
            updateRobotByReal();
            showClear();
            showToast("路径测试完成");
            isTestLine = false;
        }
    }

    //显示清除按钮
    private void showClear() {
        mode_opeleft = 2;
        tv_opeleft.setText("清除");
        tv_opeleft.setVisibility(View.VISIBLE);
    }

    //显示恢复按钮
    private void showRegain() {
        isPause=true;
        mode_opeleft = 1;
        tv_opeleft.setText("恢复");
        tv_opeleft.setVisibility(View.VISIBLE);
    }




    //显示出符合条件的prru位置
    private void drawPrruAfterTestLine() {

        //显示下行RSRP结果
        findPrru(prruInfos, Constant.prruNumber, Constant.radius);
        //根据满足门限值的prru出现次数倒序排序
        List<Map.Entry<String, MaxrsrpPosition>> lists = new ArrayList<Map.Entry<String, MaxrsrpPosition>>(mpMap.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<String, MaxrsrpPosition>>() {
            @Override
            public int compare(Map.Entry<String, MaxrsrpPosition> o1, Map.Entry<String, MaxrsrpPosition> o2) {
                float n1 = o1.getValue().getRsrp();
                float n2 = o2.getValue().getRsrp();
                if (n1 > n2) {
                    return -1;
                } else if (n1 < n2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        if(lists.size() > Constant.prruNumber){
            lists = lists.subList(0,Constant.prruNumber );
        }
        //原始逻辑
        for (Map.Entry<String, MaxrsrpPosition> entry : lists) {
            LLog.getLog().e("Prru全部",entry.getValue().toString());
            PrruGkcShape pgShape = new PrruGkcShape(entry.getKey(), Color.YELLOW, RsrpActivity.this);
            pgShape.setNecodeText(entry.getKey());
            pgShape.setPaintColor(Color.parseColor("#ff0000"));
            tempMXY = realToMap(entry.getValue().getX(), entry.getValue().getY());
            pgShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(pgShape, false);
        }
       //TODO 定制版本逻辑，非通用
        /*List<Map.Entry<String, MaxrsrpPosition>> finalPrruList = new ArrayList<>(5);
        for (Map.Entry<String, MaxrsrpPosition> entry : lists) {
            LLog.getLog().e("Prru全部",entry.getValue().toString());
            if(prruList.contains(entry.getKey())){
                finalPrruList.add(entry);
            }
        }
        //显示检验过的prru位置
        for (Map.Entry<String, MaxrsrpPosition> entry : finalPrruList) {
            PrruGkcShape pgShape = new PrruGkcShape(entry.getKey(), R.color.blue, RsrpActivity.this);
            pgShape.setNecodeText(entry.getKey());
            pgShape.setPaintColor(Color.parseColor("#ff0000"));
            tempMXY = realToMap(entry.getValue().getX(), entry.getValue().getY());
            pgShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(pgShape, false);
        }*/

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
            tempMXY = realToMap(routeList.get(i).x, routeList.get(i).y);
            CustomShape tShape = new CustomShape("routePoint" + i, R.color.blue, RsrpActivity.this, "dwf", R.mipmap.destination_point);
            tShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(tShape, false);
            if (i == 0) {
                routeLinePath.moveTo(tempMXY[0], tempMXY[1]);
            } else {
                routeLinePath.lineTo(tempMXY[0], tempMXY[1]);
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
        initDefaultCd(map, tvShowPoint, maphight);
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
                    showProgressDialog("设置中...");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            rl_add_route.setVisibility(View.GONE);
            ll_add_route.setVisibility(View.VISIBLE);
            newRouteJson = data.getStringExtra("routeJson");
            et_route.requestFocus();
        }
    }

    /**
     * 设置 PrruColor
     */
    private synchronized void setPrruColorPoint(float x,float y,int prru) {
        LLog.getLog().rsrp(x+","+y,prru+"");
        PrruInfo prruInfo = new PrruInfo();
        prruInfo.setpRRUIndex(-1);
        prruInfo.setIncludedAngle(-1);
        prruInfo.setRouteId(-1);
        prruInfo.setSlope(Double.MAX_VALUE);
        Position position = new Position();
        position.setX(x);
        position.setY(y);
        prruInfo.setPosition(position);
        prruInfo.setRsrp(prru);

        prruInfos.add(prruInfo);

        int color;
        if (-75 < prru && prru <= 0) {  //1e8449
            color = Color.parseColor("#1e8449");
        } else if (-95 < prru && prru <= -75) { //浅绿色
            color = Color.GREEN;
        } else if (-105 < prru && prru <= -95) {  //黄色
            color = Color.YELLOW;
        } else if (-120 < prru && prru <= -105) { //红色
            color = Color.RED;
        } else {
            color = Color.BLACK;
        }
        CircleShape shape = new CircleShape("rsrp"+rsrpCount, color);
        rsrpCount++;
        rsrpMXY=realToMap(x,y);
        shape.setValues(rsrpMXY[0], rsrpMXY[1]);
        map.addShape(shape, false);
    }

    //初始化地图位置
    private void initDefaultCd(final ImageMap1 map, final TextView textView, final int mapHeight) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float[] floats = realToMap(Constant.firstX, Constant.firstY, mapHeight);
                            Shape shape = new CircleShape("default111", Color.TRANSPARENT);
                            shape.setValues(floats[0], floats[1]);
                            map.addShape(shape, true);
                            textView.setText(Constant.firstX + " , " + Constant.firstY);
                            rX=Constant.firstX;
                            rY=Constant.firstY;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void expandFabMenu() {

        tv_routesetting.setVisibility(View.VISIBLE);
        ViewCompat.animate(mFucAcIv).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        isFabMenuOpen = true;
    }

    private void collapseFabMenu() {
        ViewCompat.animate(mFucAcIv).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        tv_routesetting.setVisibility(View.GONE);
        isFabMenuOpen = false;
    }

    private  void findPrru(List<PrruInfo> prruInfos, int pRRUNumber, int radius){

        calculateSlopeAndIncludedAngle(prruInfos);

        //将prruInfo按照RSRP大小降序排列
        Collections.sort(prruInfos,(PrruInfo p1, PrruInfo p2) -> (p2.getRsrp() - p1.getRsrp()));

        int prruNumner = 0;
        List<PrruInfo> result = new ArrayList<>(pRRUNumber);
        List<PrruInfo> tempDatas = new ArrayList<>();
        Map<Integer,List<PrruInfo>> routeMap = new HashMap<>();
        while(prruInfos.size() > 0 && prruNumner < pRRUNumber){
            prruNumner += 1;
            PrruInfo configPrruInfo = prruInfos.get(0);
            result.add(configPrruInfo);
            Position p1 =  configPrruInfo.getPosition();
            for (PrruInfo prruInfo : prruInfos){
                double distance = calculateDistance(p1, prruInfo.getPosition());
                if(distance < radius){
                    prruInfo.setpRRUIndex(prruNumner);
                    tempDatas.add(prruInfo);
                    if(routeMap.containsKey(prruInfo.getRouteId())){
                        routeMap.get(prruInfo.getRouteId()).add(prruInfo);
                    }else {
                        routeMap.put(prruInfo.getRouteId(),new ArrayList(Arrays.asList(prruInfo)));
                    }
                }
            }
            boolean flag = true;//优化算法开关
            if(flag && routeMap.keySet().size() > 1){
                //当前prru所在路线
               List<PrruInfo> prruInfoList = routeMap.get(configPrruInfo.getRouteId());

                algorithm(prruInfoList,routeMap, configPrruInfo, radius,tempDatas);
            }

            //移除prruInfos中prruIndex已经确定的prruInfo
            prruInfos.removeAll(tempDatas);

            tempDatas.clear();
            routeMap.clear();
        }
        //显示检验过的prru位置
        for (PrruInfo prruInfo : result) {
            PrruGkcShape pgShape = new PrruGkcShape(prruInfo.getpRRUIndex(), Color.RED, RsrpActivity.this);
            pgShape.setNecodeText(prruInfo.getpRRUIndex()+"");
            pgShape.setPaintColor(Color.parseColor("#ff0000"));
            tempMXY = realToMap(prruInfo.getPosition().getX(),prruInfo.getPosition().getY());
            pgShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(pgShape, false);
        }
    }

    private void algorithm(List<PrruInfo> prruInfoList, Map<Integer,List<PrruInfo>> routeMap, PrruInfo configPrru, int radius, List<PrruInfo> prruInfos){
        List<PrruInfo> cornerPrruInfoList = new ArrayList<>();
        for(PrruInfo prruInfo : prruInfoList){
            if(!compareDouble(prruInfo.getIncludedAngle(),-1)){
                cornerPrruInfoList.add(prruInfo);
            }
        }

        for(PrruInfo cornerPrruInfo : cornerPrruInfoList){
            //转角点与prru距离大于0.5*R
            if(calculateDistance(configPrru.getPosition(), cornerPrruInfo.getPosition()) > 0.5 * radius){
                double k = routeMap.get(0).get(0).getSlope();
                for(int i = 0; i < routeMap.keySet().size(); i++){
                    //如果该条线路和prru是同一线路则不做处理继续循环
                    if(configPrru.getRouteId() == routeMap.get(i).get(0).getRouteId()){
                        continue;
                    }
                    if(routeMap.get(i).size()>3){
                        double k1 = routeMap.get(i).get(0).getSlope();
                        if(compareDouble(k,k1)){
                            Position avgPosition = calculateAvg(routeMap.get(i));
                            if(calculateDistance(avgPosition,configPrru.getPosition()) < 8){
                                continue;
                            }
                        }

                        for(PrruInfo prruInfo : routeMap.get(i)){
                            if(calculateDistance(prruInfo.getPosition(), cornerPrruInfo.getPosition()) < 5){
                                for(PrruInfo prruInfo1 : routeMap.get(i)){
                                    prruInfo1.setpRRUIndex(-1);
                                }
                                prruInfos.removeAll(routeMap.get(i));
                                break;
                            }
                        }
                    }
                }
            }
        }


    }

    private Position calculateAvg(List<PrruInfo> prruInfos){
        Position position = new Position();
        float x = 0;
        float y = 0;
        for (PrruInfo prruInfo : prruInfos){
            x += prruInfo.getPosition().getX();
            y += prruInfo.getPosition().getY();
        }
        position.setX(x);
        position.setY(y);
        return position;
    }

    /**
     * 计算两点间距离
     * @param p1
     * @param p2
     * @return
     */
    private double calculateDistance(Position p1, Position p2){
        double a = p1.getX() - p2.getX();
        double b = p1.getY() - p2.getY();
        return Math.sqrt(a * a + b * b);
    }

    /**
     * 计算斜率和夹角
     * @param prruInfos
     */
    private void calculateSlopeAndIncludedAngle(List<PrruInfo> prruInfos){
        double x1;
        double y1;
        double x2;
        double y2;
        double k1;
        double k2;
        int routeId = 1;
        double includeAngle;
        //从第二个点开始循环计算
        for(int i = 1; i < prruInfos.size(); i++){
            //上一个相邻点的坐标
            x1 = Math.floor(prruInfos.get(i-1).getPosition().getX());
            y1 = Math.floor(prruInfos.get(i-1).getPosition().getY());

            //该点坐标
            x2 = Math.floor(prruInfos.get(i).getPosition().getX());
            y2 = Math.floor(prruInfos.get(i).getPosition().getX());

            //上一个相邻点的斜率
            k1 = prruInfos.get(i-1).getSlope();

            //计算并设置该点斜率
            if(compareDouble(x1,x2)){
                k2 = Math.tan(89);
            }else{
                k2 = (y2 - y1) / (x2 -x1);
            }
            prruInfos.get(i).setSlope(k2);

            //根据斜率值归类路线
            if(compareDouble(k1,Double.MAX_VALUE)){ //斜率为默认值则表示第一个点，将其与第二个点一起归为路线一
                prruInfos.get(i-1).setRouteId(routeId);
                prruInfos.get(i).setRouteId(routeId);
            }else if(compareDouble(k1,k2)){ //斜率一样则表示为同一路线，设置为当前线路
                prruInfos.get(i).setRouteId(routeId);
            }else{ //斜率不同则表示路线变化，线路标识自加1后设置新的路线并计算夹角
                routeId += 1;
                prruInfos.get(i).setRouteId(routeId);
                if((1 + k2 * k1) == 0){
                    includeAngle = 90.0;
                }else{
                    double tanA = (k2 - k1) / (1 + k2 * k1);
                    if(tanA > 0){
                        includeAngle = Math.atan(tanA);
                    }else {
                        includeAngle = Math.atan(tanA) + 180;
                    }
                }
                prruInfos.get(i-1).setIncludedAngle(includeAngle);
                prruInfos.get(i).setIncludedAngle(includeAngle);
            }
        }
    }

    private boolean compareDouble(double d1, double d2){
        if(Math.abs(d1-d2)>0.01){
            return false;
        }
        return true;
    }


}
