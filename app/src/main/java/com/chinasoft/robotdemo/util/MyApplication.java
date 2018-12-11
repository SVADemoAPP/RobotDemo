package com.chinasoft.robotdemo.util;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.chinasoft.robotdemo.activity.SettingActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;

public class MyApplication extends Application {

    private Context mContext;

    public void onCreate() {
        FakeX509TrustManager.allowAllSSL();
        mContext=getApplicationContext();
        Constant.mRequestQueue = Volley.newRequestQueue(mContext);
        Constant.interRequestUtil = InterRequestUtil.getInstance(mContext);
//        (SharedPrefHelper.getBoolean(mContext, "https", false)?"https://":"http://")
        Constant.IP_ADDRESS = (SharedPrefHelper.getBoolean(this, "https", true) ? "https://" : "http://")
        +SharedPrefHelper.getString(mContext, "serverIp", "218.4.33.215")
        +":"+SharedPrefHelper.getInt(mContext, "serverPort", 8083);
        Constant.sdPath=new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("robotdemo").toString();
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        FlowManager.init(new FlowConfig.Builder(this).build());
    }


}
