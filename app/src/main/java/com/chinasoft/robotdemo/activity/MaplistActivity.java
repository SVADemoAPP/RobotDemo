package com.chinasoft.robotdemo.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.adapter.MaplistAdapter;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.BlueUtils;
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.FileUtil;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.AbstractDiscover;
import com.slamtec.slamware.discovery.BleDevice;
import com.slamtec.slamware.discovery.Device;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.discovery.DiscoveryMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class MaplistActivity extends BaseActivity {

    private ListView lv_maplist;
    private List<String> mapList = new ArrayList();
    private MaplistAdapter maplistAdapter;
    private String currentMap;
    private TextView tv_next;

    private DeviceManager deviceManager;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_maplist);
    }

    @Override
    public void dealLogicBeforeInitView() {
        Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
        Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);


        File dir = new File(Constant.mapDirs);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File mapFile = new File(Constant.mapDirs + "/U9.png");
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
        maplistAdapter = new MaplistAdapter(this,mapList);
        currentMap = mapList.get(0);
    }

    @Override
    public void initView() {
        lv_maplist = findViewById(R.id.lv_maplist);
        tv_next=findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
//
        BlueUtils.getBlueUtils().getInitialization(this);
//
        BlueUtils.getBlueUtils().startBlue();

        BlueUtils.getBlueUtils().setFindBlue(new BlueUtils.FindBlue() {
            @Override
            public void getBlues(BluetoothDevice bluetoothDevice) {
                final BleDevice device = new BleDevice(bluetoothDevice);
                device.canBeFoundWith(DiscoveryMode.BLE);
                deviceManager = new DeviceManager(MaplistActivity.this);
                String wifiSSID = "PHICOMM_3201";
                String wifiPassword = "1225sznp";
                // listener; // a concrete AbstractDiscover.BleConfigureListener object
                deviceManager.pair(device, wifiSSID, wifiPassword, new AbstractDiscover.BleConfigureListener() {
                    @Override
                    public void onConfigureSuccess() {
                        Log.e("start","success");
                    }

                    @Override
                    public void onConfigureFailure(String s) {
                        Log.e("start",s);
                    }
                });

//        deviceManager.start(DiscoveryMode.MDNS);
//        BleDevice device=new BleDevice(null);
//        device.canBeFoundWith(DiscoveryMode.BLE);


                deviceManager.setListener(new AbstractDiscover.DiscoveryListener() {
                    @Override
                    public void onStartDiscovery(AbstractDiscover abstractDiscover) {

                        Log.e("start","start");
                    }

                    @Override
                    public void onStopDiscovery(AbstractDiscover abstractDiscover) {
                        Log.e("start","stop");
                    }

                    @Override
                    public void onDiscoveryError(AbstractDiscover abstractDiscover, String s) {
                        Log.e("start","error");
                    }

                    @Override
                    public void onDeviceFound(AbstractDiscover abstractDiscover, Device device) {

                    }
                });
            }
        });



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
            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                Bundle bundle=new Bundle();
                bundle.putString("currentMap",currentMap);
                openActivity(HomeActivity.class,bundle);
                finish();
                break;
            default:
                break;
        }
    }

}
