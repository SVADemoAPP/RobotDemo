package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
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
import com.chinasoft.robotdemo.view.CompassView;
import com.chinasoft.robotdemo.view.MyListView;
import com.chinasoft.robotdemo.view.popup.SuperPopupWindow;
import com.google.gson.Gson;
import com.slamtec.slamware.robot.Location;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.TouchImageView1;
import net.yoojia.imagemap.core.CircleShape;
import net.yoojia.imagemap.core.CustomShape;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.RequestShape;
import net.yoojia.imagemap.core.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PrrufindActivity extends BaseActivity implements OnRobotListener {
    private ImageMap1 map;
    private int mapHeight;
    private boolean isStart = false;
    private PrruModel nowCollectPrru;
    private int coorCount = 0;
        private Path path;
    private LineShape lineShape;
    private float lastX, lastY;
    private float nowX, nowY;
    private float[] newF, desF, mXY, rXY, tempMXY,rsrpMXY;
    private String currentMap;
    private ImageView iv_operation;
    private RequestShape robotShape;
    private CompassView cv;
    private int cStep;
    private String nowCollectNeCode;
    private float maxRsrp, xWhenMax, yWhenMax;
    private float[] xyRobotWhenMax;
    private CustomShape desShape;
    private CustomShape findShape;
    private SuperPopupWindow mSuperPopupWindow;
    private Context mContext;
    private View popupView;
    private float mapRotate;
    private float robotDirection;
    private RobotOperation ro;

    private TextView tv_forcestop, tv_prrusetting;
    private MyListView lv_userid;

    private List<PointF> nowRouteList = new ArrayList<>();

    private MyListView lv_prrulist;
    private TextView popup_prrulist_title;
    private PrruModelListAdapter prruModelListAdapter;
    private boolean isTestLine = false;
    private boolean isPause=false; //是否在暂停状态
    private int locCount;

    private float rX;
    private float rY;
    private List<PrruModel> mPrruModelList;
    private TextView tv_home_back;
    private TextView tv_opeleft;
    private int mode_opeleft = 0;//操作左边按钮的状态，0为隐藏，1为恢复，2为清除
    private SuperPopupWindow mChooseCenterPointPop;
    private String mMapName;

    private boolean connectResult = true;
    private boolean isFabMenuOpen = false;
    private ImageView mFucAcIv;
    private LocAndPrruInfoResponse lap;


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
        mContext = PrrufindActivity.this;
        setContentView(R.layout.activity_prrufind);
    }


    @Override
    public void dealLogicBeforeInitView() {
        currentMap = getIntent().getExtras().getString("currentMap");
        mPrruModelList = (List<PrruModel>) getIntent().getExtras().getSerializable("PrruModelList");
    }

    @Override
    public void initView() {
        map = findViewById(R.id.imagemap);
        iv_operation = findViewById(R.id.iv_operation);
        tv_home_back = findViewById(R.id.tv_home_back);
        tv_opeleft = findViewById(R.id.tv_opeleft);
        tv_forcestop = findViewById(R.id.tv_forcestop);
        tv_prrusetting = findViewById(R.id.tv_prrusetting);
        mFucAcIv = findViewById(R.id.fuc_menu);
        iv_operation.setOnClickListener(this);
        tv_home_back.setOnClickListener(this);
        tv_opeleft.setOnClickListener(this);
        tv_forcestop.setOnClickListener(this);
        tv_prrusetting.setOnClickListener(this);
        mFucAcIv.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {

        mMapName = currentMap;
        ro = new RobotOperation(Constant.robotIp, Constant.robotPort, currentMap, this, this, 2000);
//        ro.startOperation();

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
        cv = new CompassView(PrrufindActivity.this);
        cv.setId(0);
        cv.setImageResource(R.mipmap.icon_robot);
        cv.updateDirection(mapRotate + robotDirection);
        robotShape = new RequestShape("s", -16776961, cv, PrrufindActivity.this);
        desShape = new CustomShape("des", R.color.blue, PrrufindActivity.this, "dwf", R.mipmap.destination_point);
        findShape = new CustomShape("find", R.color.blue, PrrufindActivity.this, "dwf", R.mipmap.destination_point_gray);
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
                if (isStart &&(!isTestLine||isPause)) {
//                    ro.setShowOrbits(true);
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
        path = new Path();
        path.moveTo(x, y);
        lastX = nowX;
        lastY = nowY;
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
                    showToast("寻找Prru结束");
                    showClear();
                    try {
                        ro.forceStop();
                    } catch (Exception e) {
                        showToast("机器人异常：" + e.toString());
                    }
                } else {
                    if (mode_opeleft == 2) {
                        showToast("请先清除痕迹");
                        return;
                    }
                    if (nowCollectPrru == null) {
                        showToast("未选择Prru");
                        return;
                    }
                    iv_operation.setImageResource(R.mipmap.home_end);
                    initBeforeTest();
                    startTestLine();
                }
                break;
            case R.id.tv_prrusetting:
                if (mPrruModelList != null && mPrruModelList.size() != 0) {
                    if (mode_opeleft == 2) {
                        showToast("请先清除痕迹");
                        return;
                    }
                    initPrruListPop(mPrruModelList);     //加载弹窗视图
                } else {
                    showToast("没有Prru列表");
                }
                break;
            case R.id.tv_forcestop:
                ro.forceStop();
                if (isTestLine) {
                    showRegain();
                }
                break;
            default:
                break;
        }
    }

    private void initBeforeTest(){
//        nowCollectPrru.x=1.2f;
//        nowCollectPrru.y=0.4f;
        nowRouteList.clear();
        nowRouteList.add(new PointF(nowCollectPrru.x,nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x + 0.5f, nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y + 0.5f));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x - 0.5f, nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y - 0.5f));
        nowRouteList.add(new PointF(nowCollectPrru.x, nowCollectPrru.y));
        locCount = nowRouteList.size();
        isTestLine = true;
        tempMXY=realToMap(nowCollectPrru.x,nowCollectPrru.y);
        findShape.setValues(tempMXY[0],tempMXY[1]);
        map.addShape(findShape,false);
        maxRsrp = Float.NEGATIVE_INFINITY;
    }

    private void dealAfterTest(){
        if (maxRsrp > -10000f) {
            xyRobotWhenMax = realToMap(xWhenMax, yWhenMax);
            CircleShape maxRsrpPointShape = new CircleShape(nowCollectPrru.neCode, Color.parseColor("#ff0000"), 8f);
            maxRsrpPointShape.setValues(xyRobotWhenMax[0], xyRobotWhenMax[1]);
            map.addShape(maxRsrpPointShape, false);
        }
    }

    //清除上次的痕迹
    private void clearLastTest() {
        map.removeShape("find");
        map.removeShape(nowCollectPrru.neCode);
        map.removeShape("line");

        path.reset();
        path.moveTo(mXY[0], mXY[1]);
        lastX = nowX;
        lastY = nowY;

    }


    /***
     * 初始化开始popupwindow
     */
    private void initPrruListPop(List<PrruModel> prruModelList) {
        if (mSuperPopupWindow == null) {
            mSuperPopupWindow = new SuperPopupWindow(PrrufindActivity.this, R.layout.popup_prrulist);
            mSuperPopupWindow.setFocusable(true);
            mSuperPopupWindow.setOutsideTouchable(true);
            mSuperPopupWindow.setAnimotion(R.style.PopAnimation);
            popupView = mSuperPopupWindow.getPopupView();
            lv_prrulist = popupView.findViewById(R.id.lv_prrulist);
            popup_prrulist_title = popupView.findViewById(R.id.popup_prrulist_title);
            prruModelListAdapter = new PrruModelListAdapter(this, prruModelList);
            lv_prrulist.setAdapter(prruModelListAdapter);
            prruModelListAdapter.setPrruModelListClickListener(new PrruModelListAdapter.OnPrruModelListClickListener() {
                @Override
                public void onClick(PrruModel prruModel) {
                    nowCollectPrru = prruModel;
                    nowCollectNeCode=nowCollectPrru.neCode;
                    popup_prrulist_title.setText(nowCollectNeCode);
                    hidePrruListPop();
                }
            });
        }
        showPrruListPop();//默认开始一次
    }

    /**
     * 显示Popupwindow
     */
    private void showPrruListPop() {
        mSuperPopupWindow.showPopupWindow();
    }

    /**
     * 隐藏Popupwindow
     */
    private void hidePrruListPop() {
        mSuperPopupWindow.hidePopupWindow();
    }



    /***
     *
     * @param x
     * @param y
     * @param scaleRuler
     */
    private void saveRbLocationInfo(float x, float y, float scaleRuler) {
        SharedPrefHelper.putString(PrrufindActivity.this, "currentMap", currentMap);
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
                arriveDes();
                iv_operation.setImageResource(R.mipmap.home_start);

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
        updateRobotByReal();
//        map.setCanChange(true);
        newF = realToMap(nowX, nowY);
        path.lineTo(newF[0], newF[1]);
        lineShape.setPath(path);
        map.addShape(lineShape, false);
        lastX = nowX;
        lastY = nowY;
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
        LLog.getLog().e("异常断开", errormsg);
        //finish();
        if (isTestLine) {
            isTestLine = false;
            iv_operation.setImageResource(R.mipmap.home_start);
            showToast("寻找Prru结束");
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
//            CustomShape orbitShape = new CustomShape("coor" + i, R.color.blue, PrrufindActivity.this, "dwf", R.mipmap.orbit_point);
            CircleShape orbitShape = new CircleShape("coor" + i, Color.parseColor("#f6ddcc"), 7f);
            orbitShape.setValues(tf[0], tf[1]);
            map.addShape(orbitShape, false);
        }
    }


    @Override
    public void notifyPrru(final float x, final float y) {
        if (isTestLine) {
                Constant.interRequestUtil.getLocAndPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getLocAndPrruInfo?userId=" + Constant.userId + "&mapId="+Constant.mapId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LLog.getLog().e("getLocAndPrruInfo成功", s);
                        lap = new Gson().fromJson(s, LocAndPrruInfoResponse.class);
                        if (lap.code == 0 && lap.data.prruData != null) {
                                Float rsrp = getRsrpByGpp(nowCollectNeCode, lap.data.prruData);
                                recordMaxRsrp(rsrp, x, y);
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
        if (Math.sqrt(Math.pow((nowX - lastX),2 ) + Math.pow((nowY - lastY) ,2)) > 0.3f) {
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
//        ro.setShowOrbits(false);
        clearOrbits();
        map.removeShape("des");
        if (nowRouteList != null && nowRouteList.size() > 0) {
            if (nowRouteList.size() == locCount) {
                showToast("寻找Prru开始");
            } else if (nowRouteList.size() < locCount) {
                showToast("寻找Prru恢复");
            }
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            showToast("路径为空");
        }
    }

    private void continueTestLine(boolean isRemove) {
        if(isRemove) {
            nowRouteList.remove(0);
        }
        if (nowRouteList.size() > 0) {
            ro.moveTo(nowRouteList.get(0).x, nowRouteList.get(0).y);
        } else {
            iv_operation.setImageResource(R.mipmap.home_start);
            arriveDes();
            showClear();
            showToast("寻找Prru结束");
            dealAfterTest();
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
//                           float defaultX=SharedPrefHelper.getFloat(mContext,"firstX"); //获取设置页面设置坐标X
//                           float defaultY=SharedPrefHelper.getFloat(mContext,"firstY"); //获取设置页面设置坐标Y
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
        tv_prrusetting.setVisibility(View.VISIBLE);
        ViewCompat.animate(mFucAcIv).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        isFabMenuOpen = true;
    }

    private void collapseFabMenu() {
        ViewCompat.animate(mFucAcIv).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        tv_prrusetting.setVisibility(View.GONE);
        isFabMenuOpen = false;
    }


}
