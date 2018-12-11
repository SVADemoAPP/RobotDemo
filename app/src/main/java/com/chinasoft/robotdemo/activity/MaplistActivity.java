package com.chinasoft.robotdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.PopMapListAdapter;
import com.chinasoft.robotdemo.bean.AllPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.robot.RobotOperation;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.FileUtil;
import com.chinasoft.robotdemo.util.InterRequestUtil;
import com.chinasoft.robotdemo.util.LLog;
import com.chinasoft.robotdemo.util.PrruSubscribe;
import com.chinasoft.robotdemo.util.Subscription;
import com.chinasoft.robotdemo.util.UpLoad;
import com.chinasoft.robotdemo.util.UpdateCommunityInfo;
import com.chinasoft.robotdemo.view.popup.SuperPopupWindow;
import com.google.gson.Gson;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.discovery.DiscoveryMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class MaplistActivity extends BaseActivity {
    private List<String> mapList = new ArrayList();
    private List<PrruModel> mPrruModelList;
    private TextView   tv_setting, tv_next;
    private RadioButton mTvFucNetTest;
    private RadioButton mTvFucPrruFind;
    private RadioButton mTvFucAiPractice;
    private RadioButton mTvFucLoc;
    private RadioGroup mFucRg;
    private int mChooseMark = -1;
    private SuperPopupWindow mMapChoosePop;
    private TextView mTvMF;
    private int cNum = -1;
    private int tNum = -1;
    private String mChooseMap;
    private PopMapListAdapter mPopMapListAdapter;
    private UpLoad upLoad;
    private Subscription subscription;
    private PrruSubscribe prruSubscribe;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_maplist);
    }

    @Override
    public void dealLogicBeforeInitView() {
        upLoad=new UpLoad(this);
        subscription=new Subscription(this);
        prruSubscribe=new PrruSubscribe(this);
        Constant.userId=upLoad.getLocaIpOrMac();
//        Constant.userId="10.95.163.179";
        Constant.storeId=3;
        Constant.mapId=5122;
        File dir = new File(Constant.sdPath + "/maps/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File mapFile = new File(Constant.sdPath + "/maps/chinasoft_1f.png");
        if (!mapFile.exists()) {
            try {
                mapFile.createNewFile();
                FileUtil.writeBytesToFile(this.getAssets().open("chinasoft_1f.png"), mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        File stcmDir = new File(Constant.sdPath + "/stcms/");
//        if (!stcmDir.exists()) {
//            stcmDir.mkdirs();
//        }
//        File stcmFile = new File(Constant.sdPath + "/stcms/U9.stcm");
//        if (!stcmFile.exists()) {
//            try {
//                stcmFile.createNewFile();
//                FileUtil.writeBytesToFile(this.getAssets().open("U9.stcm"), stcmFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        String[] fileUrls = dir.list();
        for (String str : fileUrls) {
            mapList.add(str);
        }
    }

    @Override
    public void initView() {
        tv_next = findViewById(R.id.tv_next);
        tv_setting = findViewById(R.id.tv_setting);
        mTvFucNetTest = findViewById(R.id.fuc_net_test);
        mTvFucPrruFind = findViewById(R.id.fuc_prru_find);
        mTvFucAiPractice = findViewById(R.id.fuc_ai_practice);
        mTvFucLoc = findViewById(R.id.fuc_loc);
        mFucRg = findViewById(R.id.fuc_Rg);
        mTvMF = findViewById(R.id.tv_find_map);
        tv_next.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        mTvMF.setOnClickListener(this);
        mFucRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mTvFucNetTest.getId()) {
                    mChooseMark = 1;
                } else if (checkedId == mTvFucPrruFind.getId()) {
                    mChooseMark = 2;
                } else if (checkedId == mTvFucAiPractice.getId()) {
                    mChooseMark = 3;
                } else if (checkedId == mTvFucLoc.getId()) {
                    mChooseMark = 4;
                }
            }
        });
        Map<String, String> map = new HashMap();
        map.put("username", "admin");
        map.put("password", "admin");
        login(map);
    }

    private void requestPruModel() {
        Constant.interRequestUtil.getAllPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getAllPrruInfo?mapId=2046", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LLog.getLog().e("getAllPrruInfo成功", s);
                AllPrruInfoResponse ap = new Gson().fromJson(s, AllPrruInfoResponse.class);
                mPrruModelList = ap.data;
                LLog.getLog().e("prruModelList", mPrruModelList + "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LLog.getLog().e("getAllPrruInfo错误", volleyError.toString());
            }
        });
    }

    private void subscription() {
        subscription.toSubscription();
        prruSubscribe.toSubscription();
    }


    private void login(Map<String, String> map) {
        Constant.interRequestUtil.login(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/login", new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        SharedPrefHelper.putString(MaplistActivity.this, "Cookie", response.getString("Cookie"));
                        LLog.getLog().e("登录", "成功");
                        subscription();
                        requestPruModel();     //请求PruModelList信息
                        return;
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                LLog.getLog().e("登录", "错误");
                showToast("Tester登录错误");
            }
        }, map);
    }

    @Override
    public void dealLogicAfterInitView() {
        initPop();
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_setting:
                openActivityForResult(SettingActivity.class, 1);
                break;
            case R.id.tv_next:
                if (TextUtils.isEmpty(mChooseMap)) {
                    showToast("必须选择地图");
                    return;
                }
                showProgressDialog("正在连接机器人");//转圈显示连接进度
                initGlobalParams();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean connectionFlag = RobotOperation.testConnection(Constant.robotIp, Constant.robotPort);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressDialog();//转圈关闭
                                if (connectionFlag) {   //连接成功
                                    showToast("连接机器人成功");
                                    chooseActivity();
                                } else {                //连接失败
                                    showToast("连接机器人失败");
                                }
                            }
                        });
                    }
                }).start();

                break;
            case R.id.tv_find_map:
                if (mMapChoosePop != null) {
                    mPopMapListAdapter.setNum(cNum);
                    mPopMapListAdapter.notifyDataSetChanged();
                    mMapChoosePop.showPopupWindow();
                }
                break;
            default:
                break;
        }
    }

    private void initGlobalParams() {
        Constant.firstX = SharedPrefHelper.getFloat(this, "firstX", 0.0f);
        Constant.firstY = SharedPrefHelper.getFloat(this, "firstY", 0.0f);
        Constant.mapScale = SharedPrefHelper.getFloat(this, "mapScale", 20f);
        Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
        Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            Constant.IP_ADDRESS = (SharedPrefHelper.getBoolean(this, "https", false) ? "https://" : "http://")
                    + SharedPrefHelper.getString(this, "serverIp", "218.4.33.215")
                    + ":" + SharedPrefHelper.getInt(this, "serverPort", 8083);
            Map<String, String> map = new HashMap();
            map.put("username", "admin");
            map.put("password", "admin");
            login(map);
        }
    }


    private void initPop() {
        mMapChoosePop = new SuperPopupWindow(MaplistActivity.this, R.layout.popup_map_list_layout);
        mMapChoosePop.setChangFocusable(true);
        mMapChoosePop.setAnimotion(R.style.PopAnimation);
        View popupView = mMapChoosePop.getPopupView();
        ListView mapList = popupView.findViewById(R.id.pop_map_list);
        initMapList(mapList); //初始化地图maplist
        TextView confirm = popupView.findViewById(R.id.popup_confirm);
        TextView cancel = popupView.findViewById(R.id.popup_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cNum = tNum;
                mMapChoosePop.hidePopupWindow();
                mTvMF.setText(mChooseMap);
                if (Constant.mapBitmap != null) {
                    Constant.mapBitmap.recycle();
                }
                Constant.mapBitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/" + mChooseMap);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapChoosePop.hidePopupWindow();

            }
        });
    }

    private void initMapList(ListView listView) {
        mPopMapListAdapter = new PopMapListAdapter(MaplistActivity.this, mapList);
        listView.setAdapter(mPopMapListAdapter);
        mPopMapListAdapter.notifyDataSetChanged();
        mPopMapListAdapter.setOnMaplistClickListener(new PopMapListAdapter.OnMaplistClickListener() {

            @Override
            public void click(String map, int n) {
                mChooseMap = map;
                tNum = n;
            }
        });
    }

    /**
     * 选择进入不同的activity
     */
    private void chooseActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("currentMap", mChooseMap);
        switch (mChooseMark) {
            case -1:
                showToast("请选择模块");
                break;
            case 1:
                openActivity(RsrpActivity.class, bundle);
                break;
            case 2:
                bundle.putSerializable("PrruModelList", (Serializable) mPrruModelList);
                openActivity(PrrufindActivity.class, bundle);
                break;
            case 3:
                openActivity(PrrucollectActivity.class, bundle);
                break;
            case 4:
                openActivity(LocationaccActivity.class,bundle);
                break;
            default:
                break;
        }
    }

}
