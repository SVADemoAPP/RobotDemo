package com.chinasoft.robotdemo.util;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;

import java.io.File;

public class MyApplication extends Application {


    public void onCreate() {
        FakeX509TrustManager.allowAllSSL();
        Constant.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        Constant.interRequestUtil = InterRequestUtil.getInstance(getApplicationContext());
        Constant.IP_ADDRESS = SharedPrefHelper.getString(getApplicationContext(), "serverIp", "https://218.4.33.215:8083");
        Constant.sdPath=new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("robotdemo").toString();
        super.onCreate();
    }


}
