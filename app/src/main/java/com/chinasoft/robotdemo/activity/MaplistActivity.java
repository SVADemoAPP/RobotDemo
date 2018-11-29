package com.chinasoft.robotdemo.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.MaplistAdapter;
import com.chinasoft.robotdemo.bean.AllPrruInfoResponse;
import com.chinasoft.robotdemo.bean.PrruModel;
import com.chinasoft.robotdemo.entity.Floor;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.BlueUtils;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.FileUtil;
import com.chinasoft.robotdemo.util.InterRequestUtil;
import com.chinasoft.robotdemo.util.LLog;
import com.google.gson.Gson;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.AbstractDiscover;
import com.slamtec.slamware.discovery.BleDevice;
import com.slamtec.slamware.discovery.Device;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.discovery.DiscoveryMode;
import com.slamtec.slamware.discovery.MdnsDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
    private String currentMap,wifiRobotIp;
    private int wifiRobotPort,mode;
    private List<PrruModel> mPrruModelList;
    private DeviceManager deviceManager;
    private TextView tv_modecollect, tv_modefind,tv_setting,tv_next;

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
        File mapFile = new File(Constant.sdPath + "/maps/U9.png");
        if (!mapFile.exists()) {
            try {
                mapFile.createNewFile();
                FileUtil.writeBytesToFile(this.getAssets().open("U9.png"), mapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] fileUrls = dir.list();
        for (String str : fileUrls) {
            mapList.add(str);
        }
        maplistAdapter = new MaplistAdapter(this, mapList);
        currentMap = mapList.get(0);
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
        tv_next.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_modecollect.setOnClickListener(this);
        tv_modefind.setOnClickListener(this);
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
                openActivity(SettingActivity.class);
                break;
            case R.id.tv_next:
                if (mode == 0) {
                    showToast("必须选择模式");
                    return;
                }
                if (deviceManager != null) {
                    deviceManager.stop(DiscoveryMode.MDNS);
                }
                initGlobalParams();
                if (Constant.userId.equals("")) {
                    showToast("请在设置中设置UserId");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("currentMap", currentMap);
                    bundle.putSerializable("PrruModelList", (Serializable) mPrruModelList);
                    if (mode == 1) {
                        openActivity(PrrucollectActivity.class, bundle);
                    } else if (mode == 2) {
                        openActivity(PrrufindActivity.class, bundle);
                    }
                }
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
            default:
                break;
        }
    }

    private void initGlobalParams() {
        if (TextUtils.isEmpty(wifiRobotIp)) {
            Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
            Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);
        } else {
            Constant.robotIp = wifiRobotIp;
            Constant.robotPort = wifiRobotPort;
        }
        Constant.firstX = SharedPrefHelper.getFloat(this, "firstX", 0.6f);
        Constant.firstY = SharedPrefHelper.getFloat(this, "firstY", 0.3f);
        Constant.mapScale = SharedPrefHelper.getFloat(this, "mapScale", 100f);
        Constant.userId = SharedPrefHelper.getString(this, "userId", "");//临时取出赋值给UserId
        Constant.updatePeriod = SharedPrefHelper.getLong(this, "updatePeriod", 1000);
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
        if (requestCode == 1 && resultCode == -1) {
            showToast("机器人连接失败，请修改配置！");
        }
    }
}
