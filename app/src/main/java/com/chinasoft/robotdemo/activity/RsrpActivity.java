package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
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
import com.chinasoft.robotdemo.bean.PrruModel;
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
//    private Map<String,MaxrsrpPosition> prruMap=new HashMap<>();    //存放prru位置的map
//    private MaxrsrpPosition tempPosition;

    @Override
    public void setContentLayout() {
        mContext = RsrpActivity.this;
        setContentView(R.layout.activity_rsrp);
    }


    @Override
    public void dealLogicBeforeInitView() {
//        prruMap.put("0_80_1",new MaxrsrpPosition(64.07f,51.93f));
//        prruMap.put("0_81_1",new MaxrsrpPosition(31.7f,74f));
//        prruMap.put("0_82_1",new MaxrsrpPosition(31.6f,30.6f));
//        prruMap.put("0_83_1",new MaxrsrpPosition(53.5f,30.75f));
//        prruMap.put("0_84_1",new MaxrsrpPosition(20.3f,52.6f));

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
        if (prruSigalModelList == null) {
            return;
        }
        for (PrruSigalModel psm : prruSigalModelList) {
            if (psm.rsrp > -950) {
                LLog.getLog().robot(x + "," + y, psm.gpp + "____" + psm.rsrp);
                if (!mpMap.keySet().contains(psm.gpp)) {
                    MaxrsrpPosition mp = new MaxrsrpPosition();
                    mp.setNum(1); //出现次数为1
                    mp.setX(x);
                    mp.setY(y);
                    mp.setRsrp(psm.rsrp);
                    mpMap.put(psm.gpp, mp);
                } else {
                    tempMp = mpMap.get(psm.gpp);
                    tempMp.setNum(tempMp.getNum()+1); //出现次数+1
                    if (psm.rsrp > tempMp.getRsrp()) { //判断是否更新最大rsrp值及位置
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
                setPrruColorPoint(x,y, Long.parseLong(updateCommunityInfo.RSRP));
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




    //mpMap按rsrp从大到小排序并画出前5个
    private void drawPrruAfterTestLine() {
        List<Map.Entry<String, MaxrsrpPosition>> lists = new ArrayList<Map.Entry<String, MaxrsrpPosition>>(mpMap.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<String, MaxrsrpPosition>>() {
            @Override
            public int compare(Map.Entry<String, MaxrsrpPosition> o1, Map.Entry<String, MaxrsrpPosition> o2) {
                float n1 = o1.getValue().getNum();
                float n2 = o2.getValue().getNum();
                if (n1 > n2) {
                    return -1;
                } else if (n1 < n2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
//        for (Map.Entry<String, MaxrsrpPosition> entry : lists.size() > 5 ? lists.subList(0, 5) : lists) {
        for (Map.Entry<String, MaxrsrpPosition> entry : lists) {
//            LLog.getLog().e("Prru次数前5",entry.getValue().toString());
            LLog.getLog().e("Prru全部",entry.getValue().toString());
//            if(prruMap.containsKey(entry.getKey())){
//                tempPosition=prruMap.get(entry.getKey());
//                //如果距离小于8米，则替换prruMap中的位置
//                if(Math.sqrt(Math.pow(tempPosition.getX()-entry.getValue().getX(),2)+Math.pow(tempPosition.getY()-entry.getValue().getY(),2))<8f){
//                    prruMap.put(entry.getKey(),entry.getValue());
//                }
//            }
            PrruGkcShape pgShape = new PrruGkcShape(entry.getKey(), R.color.blue, RsrpActivity.this);
            pgShape.setNecodeText(entry.getKey());
            pgShape.setPaintColor(Color.parseColor("#ff0000"));
            tempMXY = realToMap(entry.getValue().getX(), entry.getValue().getY());
            pgShape.setValues(tempMXY[0], tempMXY[1]);
            map.addShape(pgShape, false);
        }
//        //显示检验过的prru位置
//        for (Map.Entry<String, MaxrsrpPosition> entry : prruMap.entrySet()) {
//            PrruGkcShape pgShape = new PrruGkcShape(entry.getKey(), R.color.blue, RsrpActivity.this);
//            pgShape.setNecodeText(entry.getKey());
//            pgShape.setPaintColor(Color.parseColor("#ff0000"));
//            tempMXY = realToMap(entry.getValue().getX(), entry.getValue().getY());
//            pgShape.setValues(tempMXY[0], tempMXY[1]);
//            map.addShape(pgShape, false);
//
//        }

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
    private synchronized void setPrruColorPoint(float x,float y,long prru) {
        LLog.getLog().rsrp(x+","+y,prru+"");
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


}
