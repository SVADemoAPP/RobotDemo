package com.chinasoft.robotdemo.util;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;

public class MyApplication extends Application {
    private static MyApplication instance;
    private String filePath;

    public static MyApplication getApplication() {
        return instance;
    }

    public void onCreate() {
        FakeX509TrustManager.allowAllSSL();
        Constant.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        InterRequestUtil.getInstance(getApplicationContext());
        Constant.IP_ADDRESS = SharedPrefHelper.getString(getApplicationContext(), "ip", "https://218.4.33.215:8083");
        Constant.toast = Toast.makeText(this, "", 0);
        super.onCreate();
        instance = this;
        this.filePath = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append("/").append(instance.getApplicationContext().getPackageName()).append("/").toString();
        CrashHelper.getInstance().init(getApplicationContext());
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getRoutesPath() {
        return this.filePath + "result_store";
    }
}
