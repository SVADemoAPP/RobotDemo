package com.chinasoft.robotdemo.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.LLog;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class SettingActivity extends BaseActivity {

    private TextView mTvExit, mTvReset, mTvSave;
    private TextView mTvhttp;
    private TextView mTvHttps;
    private boolean httpsFlag;  //false 表示http请求,true 表示https请求
    private EditText mEdtRbIp;
    private EditText mEdtRbPort;
    private EditText mEdtServerIp;
    private EditText mEdtServerPort;
    private TextView mEdtSettingScale;
    private TextView mEdtSettingPointX;
    private TextView mEdtSettingPointY;
    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void initView() {
        mTvExit = findViewById(R.id.tv_exit);
        mTvReset = findViewById(R.id.tv_reset);
        mTvSave = findViewById(R.id.tv_save);
        mTvhttp = findViewById(R.id.tv_http);
        mTvHttps = findViewById(R.id.tv_https);
        mEdtRbIp = findViewById(R.id.et_rb_ip);
        mEdtRbPort = findViewById(R.id.et_rb_port);
        mEdtServerIp = findViewById(R.id.et_server_ip);
        mEdtServerPort = findViewById(R.id.et_server_port);
        mEdtSettingScale = findViewById(R.id.edt_setting_scale);
        mEdtSettingPointX = findViewById(R.id.edt_setting_pointX);
        mEdtSettingPointY = findViewById(R.id.edt_setting_pointY);
        mTvExit.setOnClickListener(this);
        mTvReset.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mTvhttp.setOnClickListener(this);
        mTvHttps.setOnClickListener(this);
        defaultData();
    }

    @Override
    public void dealLogicAfterInitView() {
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                finish();
                break;
            case R.id.tv_reset:
                resetData();
                break;
            case R.id.tv_save:
                saveData();

                break;
            case R.id.tv_http:
                httpsFlag = false;
                setHttpState(httpsFlag);
                break;
            case R.id.tv_https:
                httpsFlag = true;
                setHttpState(httpsFlag);
                break;
            default:
                break;
        }
    }

    /**
     * @param flag 请求状态
     */
    private void setHttpState(boolean flag) {
        if (flag) {  //https请求方式
            mTvhttp.setTextColor(Color.WHITE);
            mTvHttps.setTextColor(Color.parseColor("#00b5ff"));
        } else {    //http请求方式
            mTvhttp.setTextColor(Color.parseColor("#00b5ff"));
            mTvHttps.setTextColor(Color.WHITE);
        }
    }

    /**
     * 设置默认设置
     */
    private void defaultData() {
        mEdtRbIp.setText(SharedPrefHelper.getString(SettingActivity.this, "robotIp", ""));
        mEdtRbPort.setText(SharedPrefHelper.getInt(SettingActivity.this, "robotPort")==-1?"":String.valueOf(SharedPrefHelper.getInt(SettingActivity.this, "robotPort")));
        mEdtServerIp.setText(SharedPrefHelper.getString(SettingActivity.this, "serverIp", ""));
        mEdtServerPort.setText(SharedPrefHelper.getInt(SettingActivity.this, "serverPort")==-1?"":String.valueOf(SharedPrefHelper.getInt(SettingActivity.this, "serverPort")));
        mEdtSettingPointX.setText(SharedPrefHelper.getFloat(SettingActivity.this, "firstX")==-1?"":String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "firstX")));
        mEdtSettingPointY.setText(SharedPrefHelper.getFloat(SettingActivity.this, "firstY")==-1?"":String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "firstY")));
        mEdtSettingScale.setText(SharedPrefHelper.getFloat(SettingActivity.this, "mapScale")==-1?"":String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "mapScale")));
        httpsFlag = SharedPrefHelper.getBoolean(SettingActivity.this, "https", false);
        setHttpState(httpsFlag);
        mEdtServerIp.requestFocus();
    }

    /***
     * 重置数据
     */
    private void resetData() {
        mEdtRbIp.setText("");
        mEdtRbPort.setText("");
        mEdtServerIp.setText("");
        mEdtServerPort.setText("");
        mEdtSettingPointX.setText("");
        mEdtSettingPointY.setText("");
        mEdtSettingScale.setText("");
        httpsFlag = false;
        setHttpState(httpsFlag);
        mEdtServerIp.requestFocus();
    }

    /**
     * 保存设置  分别存储shareprefence
     */
    private void saveData() {
        String serverIp = mEdtServerIp.getText().toString().trim();
        String serverPort = mEdtServerPort.getText().toString().trim();
        String rbIp = mEdtRbIp.getText().toString().trim();
        String rbPort = mEdtRbPort.getText().toString().trim();
        String pointX = mEdtSettingPointX.getText().toString().trim();
        String pointY = mEdtSettingPointY.getText().toString().trim();
        String scale = mEdtSettingScale.getText().toString().trim();
        if (TextUtils.isEmpty(serverIp)) {
            mEdtServerIp.requestFocus();
            showToast("请填写服务器IP");
            return;
        }
        if (TextUtils.isEmpty(serverPort)) {
            mEdtServerPort.requestFocus();
            showToast("请填写服务器端口");
            return;
        }
        if (TextUtils.isEmpty(rbIp)) {
            mEdtRbIp.requestFocus();
            showToast("请填写机器人IP");
            return;
        }
        if (TextUtils.isEmpty(rbPort)) {
            mEdtRbPort.requestFocus();
            showToast("请填写机器人端口");
            return;
        }
        if (TextUtils.isEmpty(pointX)) {
            mEdtSettingPointX.requestFocus();
            showToast("请填写初始X坐标");
            return;
        }
        if (TextUtils.isEmpty(pointY)) {
            mEdtSettingPointY.requestFocus();
            showToast("请填写初始Y坐标");
            return;
        }
        if (TextUtils.isEmpty(scale)) {
            mEdtSettingScale.requestFocus();
            showToast("请填写地图比例尺");
            return;
        }
        SharedPrefHelper.putBoolean(SettingActivity.this, "https", httpsFlag);
        SharedPrefHelper.putString(SettingActivity.this, "serverIp", serverIp);
        SharedPrefHelper.putInt(SettingActivity.this, "serverPort", Integer.parseInt(serverPort));
        SharedPrefHelper.putString(SettingActivity.this, "robotIp", rbIp);
        SharedPrefHelper.putInt(SettingActivity.this, "robotPort", Integer.valueOf(rbPort));
        SharedPrefHelper.putFloat(SettingActivity.this, "firstX", Float.valueOf(pointX));
        SharedPrefHelper.putFloat(SettingActivity.this, "firstY", Float.valueOf(pointY));
        SharedPrefHelper.putFloat(SettingActivity.this, "mapScale", Float.valueOf(scale));
        showToast("保存成功");
        setResult(2);
        finish();
    }
}
