package com.chinasoft.robotdemo.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class SettingActivity extends BaseActivity {

private TextView tv_exit,tv_reset,tv_save;
    private TextView tv_http;
    private TextView tv_https;
    private boolean  httpsFlag;  //false 表示http请求,true 表示https请求
    private static final String ROBOT_IP="192.168.11.1";    //机器人iP
    private static final String ROBOT_PORT="1445";          //机器人端口
    private static final String SERVER_IP="192.168.11.1";   //服务器iP
    private static final String SERVER_PORT="3880";         //服务器端口
    private EditText et_rb_ip;
    private EditText et_rb_port;
    private EditText et_server_ip;
    private EditText et_server_port;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void initView() {
        tv_exit=findViewById(R.id.tv_exit);
        tv_reset=findViewById(R.id.tv_reset);
        tv_save=findViewById(R.id.tv_save);
        tv_http = findViewById(R.id.tv_http);
        tv_https = findViewById(R.id.tv_https);
        et_rb_ip =findViewById(R.id.et_rb_ip);
        et_rb_port =findViewById(R.id.et_rb_port);
        et_server_ip =findViewById(R.id.et_server_ip);
        et_server_port =findViewById(R.id.et_server_port);
        tv_exit.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_http.setOnClickListener(this);
        tv_https.setOnClickListener(this);
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
                defaultData();
                break;
            case R.id.tv_save:
                saveData();
                break;
            case R.id.tv_http:
                httpsFlag=false;
                setHttpState(httpsFlag);
                break;
            case R.id.tv_https:
                httpsFlag=true;
                setHttpState(httpsFlag);
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param flag 请求状态
     */
    private  void setHttpState(boolean flag){
        if(flag){  //https请求方式
            tv_http.setTextColor(Color.WHITE);
            tv_https.setTextColor(Color.parseColor("#00b5ff"));
        }else {    //http请求方式
            tv_http.setTextColor(Color.parseColor("#00b5ff"));
            tv_https.setTextColor(Color.WHITE);
        }
    }
    /**
     * 设置默认设置
     */
    private void defaultData(){
        et_rb_ip.setText(ROBOT_IP);
        et_rb_ip.setSelection(et_rb_ip.getText().length());
        et_rb_port.setText(ROBOT_PORT);
        et_rb_port.setSelection(et_rb_port.getText().length());
        et_server_ip.setText(SERVER_IP);
        et_server_ip.setSelection(et_server_ip.getText().length());
        et_server_port.setText(SERVER_PORT);
        et_server_port.setSelection(et_server_port.getText().length());
        httpsFlag=false;
        setHttpState(httpsFlag);
        et_rb_ip.requestFocus();
    }

    /**
     * 保存设置  分别存储shareprefence
     */
    private void saveData(){
        String rbIp=et_rb_ip.getText().toString();
        String rbPort=et_rb_port.getText().toString();
        String serverIp=et_server_ip.getText().toString();
        String serPort=et_server_port.getText().toString();
        SharedPrefHelper.putString(SettingActivity.this, "robotIp", rbIp);
        SharedPrefHelper.putString(SettingActivity.this, "robotPort", rbPort);
        SharedPrefHelper.putString(SettingActivity.this, "serverIp", serverIp);
        SharedPrefHelper.putString(SettingActivity.this, "serPort", serPort);
        SharedPrefHelper.putBoolean(SettingActivity.this, "https", httpsFlag);
    }
}
