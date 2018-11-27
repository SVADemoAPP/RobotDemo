package com.chinasoft.robotdemo.activity;

import android.graphics.Color;
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
    private static final String ROBOT_IP = "192.168.11.1";    //机器人iP
    private static final String ROBOT_PORT = "1445";          //机器人端口
    private static final String SERVER_IP = "192.168.11.1";   //服务器iP
    private static final String SERVER_PORT = "3880";         //服务器端口
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
                finish();
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
        LLog.getLog().e("测试", "测试1");
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
//        mEdtRbIp.setText(ROBOT_IP);
//        mEdtRbIp.setSelection(mEdtRbIp.getText().length());
//        mEdtRbPort.setText(ROBOT_PORT);
//        mEdtRbPort.setSelection(mEdtRbPort.getText().length());
//        mEdtServerIp.setText(SERVER_IP);
//        mEdtServerIp.setSelection(mEdtServerIp.getText().length());
//        mEdtServerPort.setText(SERVER_PORT);
//        mEdtServerPort.setSelection(mEdtServerPort.getText().length());
//        mEdtSettingPointX.setText("");
//        mEdtSettingPointY.setText("");
//        mEdtSettingScale.setText("");
        mEdtRbIp.setText(SharedPrefHelper.getString(SettingActivity.this, "robotIp", ""));
        mEdtRbPort.setText(String.valueOf(SharedPrefHelper.getInt(SettingActivity.this, "robotPort", 0)));
        mEdtServerIp.setText(SharedPrefHelper.getString(SettingActivity.this, "userId", ""));
        mEdtServerPort.setText(String.valueOf(SharedPrefHelper.getInt(SettingActivity.this, "serPort", 0)));
        mEdtSettingPointX.setText(String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "firstX", 0f)));
        mEdtSettingPointY.setText(String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "firstY", 0f)));
        mEdtSettingScale.setText(String.valueOf(SharedPrefHelper.getFloat(SettingActivity.this, "mapScale", 0f)));
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

        String rbIp = mEdtRbIp.getText().toString().trim();
        String rbPort = mEdtRbPort.getText().toString().trim();
        String serverIp = mEdtServerIp.getText().toString().trim();
        String serPort = mEdtServerPort.getText().toString().trim();
        String scale = mEdtSettingScale.getText().toString().trim();
        String pointX = mEdtSettingPointX.getText().toString().trim();
        String pointY = mEdtSettingPointY.getText().toString().trim();

        if (rbIp == null || rbIp.equals("")) {
            mEdtRbIp.requestFocus();
            mEdtRbIp.setError("请填写机器人IP");
        } else if (rbPort == null || rbPort.equals("")) {
            mEdtRbPort.requestFocus();
            mEdtRbPort.setError("请填写机器人端口");
        } else if (serverIp == null || serverIp.equals("")) {
            mEdtServerIp.requestFocus();
            mEdtServerIp.setError("请填写服务器IP");
        } else if (serPort == null || serPort.equals("")) {
            mEdtServerPort.requestFocus();
            mEdtServerPort.setError("请填写服务器端口");
        } else if (scale == null || scale.equals("")) {
            mEdtSettingScale.requestFocus();
            mEdtSettingScale.setError("请填写地图比例尺");
        } else if (pointX == null || pointX.equals("")) {
            mEdtSettingPointX.requestFocus();
            mEdtSettingPointX.setError("请填写X坐标");
        } else if (pointY == null || pointY.equals("")) {
            mEdtSettingPointY.requestFocus();
            mEdtSettingPointY.setError("请填写机器人IP");
        } else {
            SharedPrefHelper.putString(SettingActivity.this, "robotIp", rbIp);
            SharedPrefHelper.putInt(SettingActivity.this, "robotPort", Integer.valueOf(rbPort));
            SharedPrefHelper.putString(SettingActivity.this, "userId", serverIp);
            SharedPrefHelper.putInt(SettingActivity.this, "serPort", Integer.parseInt(serPort));
            SharedPrefHelper.putBoolean(SettingActivity.this, "https", httpsFlag);
            SharedPrefHelper.putFloat(SettingActivity.this, "firstX", Float.valueOf(pointX));
            SharedPrefHelper.putFloat(SettingActivity.this, "firstY", Float.valueOf(pointY));
            SharedPrefHelper.putFloat(SettingActivity.this, "mapScale", Float.valueOf(scale));
            showToast("保存成功");
        }

    }
}
