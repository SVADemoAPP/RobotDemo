package com.chinasoft.robotdemo.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.chinasoft.robotdemo.util.Constant;
import com.chinasoft.robotdemo.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class MapselectActivity extends BaseActivity {

    private ListView lv_maps;
    private List<String> mapList = new ArrayList();
    private MaplistAdapter maplistAdapter;
    private String currentMap;
    private ImageView iv_setting;
    private Button bt_confirm;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_mapselect);
    }

    @Override
    public void dealLogicBeforeInitView() {
        Constant.sdPath = SharedPrefHelper.getString(this, "sdPath", "/sdcard/robotdemo");
        Constant.robotIp = SharedPrefHelper.getString(this, "robotIp", "192.168.11.1");
        Constant.robotPort = SharedPrefHelper.getInt(this, "robotPort", 1445);

        maplistAdapter = new MaplistAdapter(this);
        File dir = new File(Constant.sdPath + "/maps");
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

        String[] fileUrls = dir.list();
        for (String str : fileUrls) {
            mapList.add(str);
        }
        currentMap = mapList.get(0);
        maplistAdapter.setMapList(mapList);
    }

    @Override
    public void initView() {
        lv_maps = findViewById(R.id.lv_maps);
        iv_setting = findViewById(R.id.iv_setting);
        bt_confirm=findViewById(R.id.bt_confirm);
        iv_setting.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
        maplistAdapter.setCurrentMap(currentMap);
        lv_maps.setAdapter(maplistAdapter);
        lv_maps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentMap = mapList.get(position);
                maplistAdapter.setCurrentMap(currentMap);
                maplistAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                openActivityForResult(SettingActivity.class, 200);
                break;
            case R.id.bt_confirm:
                Bundle bundle=new Bundle();
                bundle.putString("currentMap",currentMap);
                openActivity(HomeActivity.class,bundle);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == -1) {
            showToast("修改成功");
            refreshMapList(Constant.sdPath + "/maps");
        }
    }

    private void refreshMapList(String sdPath) {
        File dir = new File(sdPath);
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
        mapList.clear();
        String[] fileUrls = dir.list();
        for (String str : fileUrls) {
            mapList.add(str);
        }
        currentMap = mapList.get(0);
        maplistAdapter.setCurrentMap(currentMap);
        maplistAdapter.setMapList(mapList);
        maplistAdapter.notifyDataSetChanged();
    }

}
