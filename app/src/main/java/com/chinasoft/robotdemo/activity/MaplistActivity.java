package com.chinasoft.robotdemo.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.chinasoft.robotdemo.adapter.MaplistAdapter;
import com.chinasoft.robotdemo.adapter.PopMapListAdapter;
import com.chinasoft.robotdemo.bean.AllPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.FileUtil;
import com.chinasoft.robotdemo.util.InterRequestUtil;
import com.chinasoft.robotdemo.util.LLog;
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
    private ListView lv_maplist;
    private List<String> mapList = new ArrayList();
    private MaplistAdapter maplistAdapter;
    private String currentMap, wifiRobotIp;
    private int wifiRobotPort;
    private int mode = 1;
    private List<PrruModel> mPrruModelList;
    private DeviceManager deviceManager;
    private TextView tv_modecollect, tv_modefind, tv_setting, tv_next;
    private RadioButton mTvFucNetTest;
    private RadioButton mTvFucPrruFind;
    private RadioButton mTvFucAiPractice;
    private RadioButton mTvFucLoc;
    private RadioGroup mFucRg;
    private int mChooseMark = -1;
    private SuperPopupWindow mMapChoosePop;
    private TextView mTvMF;
    private static final int[] ATTRS = new int[] {
            android.R.attr.fastScrollThumbDrawable,
    };
    private  int cNum=-1;
    private  int tNum=-1;
    private String  mChooseMap;
    private PopMapListAdapter mPopMapListAdapter;
    //    private AbstractDiscover.DiscoveryListener discoveryListener = new AbstractDiscover.DiscoveryListener() {
//        @Override
//        public void onStartDiscovery(AbstractDiscover abstractDiscover) {
//            LLog.getLog().e("discoveryListener", "onStartDiscovery");
//        }
//
//        @Override
//        public void onStopDiscovery(AbstractDiscover abstractDiscover) {
//            LLog.getLog().e("discoveryListener", "onStopDiscovery");
//        }
//
//        @Override
//        public void onDiscoveryError(AbstractDiscover abstractDiscover, String s) {
//            LLog.getLog().e("discoveryListener", "错误：" + s);
//        }
//
//        @Override
//        public void onDeviceFound(AbstractDiscover abstractDiscover, Device device) {
//            LLog.getLog().e("discoveryListener", "找到device：" + device.toString());
//            if (device instanceof BleDevice) {
////                                    Log.e("msg","aa"+((BleDevice)device).getDevice());
//            } else if (device instanceof MdnsDevice) {
//                wifiRobotIp = ((MdnsDevice) device).getAddr();
//                wifiRobotPort = ((MdnsDevice) device).getPort();
//            }
//        }
//    };


    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_maplist);
    }

    @Override
    public void dealLogicBeforeInitView() {

        Constant.interRequestUtil = InterRequestUtil.getInstance(this);

        File dir = new File(Constant.sdPath + "/maps/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File mapFile = new File(Constant.sdPath + "/maps/U5_2F.png");
        if (!mapFile.exists()) {
            try {
                mapFile.createNewFile();
                FileUtil.writeBytesToFile(this.getAssets().open("U5_2F.png"), mapFile);
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
        maplistAdapter = new MaplistAdapter(this, mapList);
//        currentMap = mapList.get(0);
        showProgressDialog("地图识别中...");
        Constant.mapBitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/" + currentMap);
        dismissProgressDialog();
    }

    @Override
    public void initView() {
        lv_maplist = findViewById(R.id.lv_maplist);
        tv_next = findViewById(R.id.tv_next);
        tv_setting = findViewById(R.id.tv_setting);
        tv_modecollect = findViewById(R.id.tv_modecollect);
        tv_modefind = findViewById(R.id.tv_modefind);

        mTvFucNetTest = findViewById(R.id.fuc_net_test);
        mTvFucPrruFind = findViewById(R.id.fuc_prru_find);
        mTvFucAiPractice = findViewById(R.id.fuc_ai_practice);
        mTvFucLoc = findViewById(R.id.fuc_loc);
        mFucRg = findViewById(R.id.fuc_Rg);

        mTvMF = findViewById(R.id.tv_find_map);
        tv_next.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_modecollect.setOnClickListener(this);
        tv_modefind.setOnClickListener(this);
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
                Log.e("XHF", mChooseMark + "");
            }
        });
//        BlueUtils.getBlueUtils().setFindBlue(new BlueUtils.FindBlue() {
//            @Override
//            public void getBlues(BluetoothDevice bluetoothDevice) {
//
//                final BleDevice device = new BleDevice(bluetoothDevice);
//                boolean f= device.canBeFoundWith(DiscoveryMode.MDNS);
//                String wifiSSID = "LampSite";
//                String wifiPassword = "1225sznp";
//                // listener; // a concrete AbstractDiscover.BleConfigureListener object
//                deviceManager.pair(device, wifiSSID, wifiPassword, new AbstractDiscover.BleConfigureListener() {
//                    @Override
//                    public void onConfigureSuccess() {
//                        Log.e("start","success");
////                        deviceManager.setListener(discoveryListener);
////                        deviceManager.start(DiscoveryMode.BLE);
////                        deviceManager.start(DiscoveryMode.MDNS);
//                    }
//
//                    @Override
//                    public void onConfigureFailure(String s) {
//                        Log.e("start",s);
//                    }
//                });
//            }
//        });
//        BlueUtils.getBlueUtils().getInitialization(this);
////
//        BlueUtils.getBlueUtils().startBlue();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                deviceManager = new DeviceManager(MaplistActivity.this);
//                deviceManager.setListener(discoveryListener);
//                deviceManager.start(DiscoveryMode.MDNS);
//            }
//        }).start();
        Map<String, String> map = new HashMap();
        map.put("username", "admin");
        map.put("password", "admin");
        login(map);
    }
////        });

    private void requestPruModel() {
        Constant.interRequestUtil.getAllPrruInfo(Request.Method.POST, Constant.IP_ADDRESS + "/tester/app/prruPhoneApi/getAllPrruInfo?mapId=2046", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LLog.getLog().e("getAllPrruInfo成功", s);
                AllPrruInfoResponse ap = new Gson().fromJson(s, AllPrruInfoResponse.class);
//                        P lap=new Gson().fromJson(s,LocAndPrruInfoResponse.class);
//                        if(lap.code==0) {
//                            LLog.getLog().prru( "," , prruDataToString(lap.data.prruData));
//                        }
                mPrruModelList = ap.data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                LLog.getLog().e("prruModelList", mPrruModelList + "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LLog.getLog().e("getAllPrruInfo错误", volleyError.toString());
            }
        });
    }


    private void login(Map<String, String> map) {
        Constant.interRequestUtil.login(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/login", new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        SharedPrefHelper.putString(MaplistActivity.this, "Cookie", response.getString("Cookie"));
                        LLog.getLog().e("登录", "成功");
                        showToast("Tester登录成功");
                        requestPruModel();     //请求PruModelList信息
                        //                        Constant.interRequestUtil.getMapData(
//                                response.getString("Cookie"), 1,
//                                Constant.IP_ADDRESS
//                                        + "/tester/api/app/getMapDataByIp",
//                                new Response.Listener<JSONObject>() {
//                                    public void onResponse(JSONObject jsonobj) {
//                                        Log.e("地图数据",
//                                                jsonobj.toString());
//                                        try {
//                                            JSONArray array = jsonobj
//                                                    .getJSONArray("data");
//                                            int i = 0;
//                                            while (i < array.length()) {
//                                                try {
//                                                    Floor f = new Floor();
//                                                    JSONObject o = array
//                                                            .getJSONObject(i);
//                                                    f.setFloor(o
//                                                            .getString("floor"));
//                                                    f.setPath(o
//                                                            .getString("path"));
//                                                    f.setPlace(o
//                                                            .getString("place"));
//                                                    f.setXo(Float.valueOf(
//                                                            o.getString("xo"))
//                                                            .floatValue());
//                                                    f.setYo(Float.valueOf(
//                                                            o.getString("yo"))
//                                                            .floatValue());
//                                                    f.setScale(Float
//                                                            .valueOf(
//                                                                    o.getString("scale"))
//                                                            .floatValue());
//                                                    f.setUpdateTime(o
//                                                            .getString("updateTime"));
//                                                    f.setCoordinate(o
//                                                            .getString("coordinate"));
//                                                    f.setImgWidth(o
//                                                            .getInt("imgWidth"));
//                                                    f.setImgHeight(o
//                                                            .getInt("imgHeight"));
//                                                    f.setAngle((float) o
//                                                            .getInt("angle"));
//                                                    f.setId(o.getString("id"));
//                                                    f.setMapId(o
//                                                            .getString("mapId"));
//                                                    f.setSiteId(o
//                                                            .getInt("siteId"));
//                                                    Constant.mapData.add(f);
//                                                    i++;
//                                                } catch (JSONException e) {
//                                                    return;
//                                                }
//                                            }
//                                            for (Floor f : Constant.mapData) {
//                                                if (f.getMapId().equals("2046")) {
//                                                    Constant.currentFloor = f;
//                                                    break;
//                                                }
//                                            }
//                                        } catch (Exception e2) {
//                                        }
//                                    }
//                                }, new Response.ErrorListener() {
//                                    public void onErrorResponse(
//                                            VolleyError error) {
//                                    }
//                                });

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
        maplistAdapter.setCurrentMap(currentMap);
        lv_maplist.setAdapter(maplistAdapter);
        maplistAdapter.setOnMaplistClickListener(new MaplistAdapter.OnMaplistClickListener() {
            @Override
            public void click(String map) {
                currentMap = map;
                maplistAdapter.setCurrentMap(currentMap);
                maplistAdapter.notifyDataSetChanged();
                showProgressDialog("切换中...");
                Constant.mapBitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/" + currentMap);
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_setting:
                openActivityForResult(SettingActivity.class, 1);
                break;
            case R.id.tv_next:
//                if (mode == 0) {
//                    showToast("必须选择模式");
//                    return;
//                }
                if (deviceManager != null) {
                    deviceManager.stop(DiscoveryMode.MDNS);
                }
                initGlobalParams();
                if(TextUtils.isEmpty(mChooseMap)){
                    showToast("必须选择地图");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("currentMap", mChooseMap);
                switch (mChooseMark){
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
                        openActivity(RouteActivity.class);
                        break;
                    default:
                        break;
                }
//                if (mode == 1) {
//                    openActivity(PrrucollectActivity.class, bundle);
//                } else if (mode == 2) {
//                    bundle.putSerializable("PrruModelList", (Serializable) mPrruModelList);
//                    openActivity(PrrufindActivity.class, bundle);
//                }
                break;
            case R.id.tv_modecollect:
                mode = 1;
                tv_modecollect.setBackgroundResource(R.mipmap.tv_mode_select);
                tv_modefind.setBackgroundResource(R.mipmap.tv_mode_unselect);
                break;
            case R.id.tv_modefind:
                mode = 2;
                tv_modecollect.setBackgroundResource(R.mipmap.tv_mode_unselect);
                tv_modefind.setBackgroundResource(R.mipmap.tv_mode_select);
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
//        if (TextUtils.isEmpty(wifiRobotIp)) {
//            Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
//            Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);
//        } else {
//            Constant.robotIp = wifiRobotIp;
//            Constant.robotPort = wifiRobotPort;
//        }
        Constant.firstX = SharedPrefHelper.getFloat(this, "firstX", 0.6f);
        Constant.firstY = SharedPrefHelper.getFloat(this, "firstY", 0.3f);
        Constant.mapScale = SharedPrefHelper.getFloat(this, "mapScale", 100f);
        Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
        Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);

        Constant.updatePeriod = SharedPrefHelper.getLong(this, "updatePeriod", 2000);
        Constant.lineSpace = SharedPrefHelper.getFloat(this, "lineSpace", 0.3f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.stop(DiscoveryMode.MDNS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
//            (SharedPrefHelper.getBoolean(this, "https", false) ? "https://" : "http://")
            Constant.IP_ADDRESS = "https://"
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
                cNum=tNum;
                mMapChoosePop.hidePopupWindow();
                mTvMF.setText(mChooseMap);
                Constant.mapBitmap = BitmapFactory.decodeFile(Constant.sdPath + "/maps/" + mChooseMap);
//                SharedPrefHelper.putString(MaplistActivity.this, "currentMap", mChooseMap);
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
            public void click(String map,int n) {
                mChooseMap=map;
                tNum=n;
                Log.e("XHF",mChooseMap);
            }
        });
    }


}
