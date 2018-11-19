package com.chinasoft.robotdemo.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHelper implements UncaughtExceptionHandler {
    private static CrashHelper sInstance = null;
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    public static CrashHelper getInstance() {
        if (sInstance == null) {
            synchronized (CrashHelper.class) {
                if (sInstance == null) {
                    synchronized (CrashHelper.class) {
                        sInstance = new CrashHelper();
                    }
                }
            }
        }
        return sInstance;
    }

    private CrashHelper() {
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread t, Throwable e) {
        handleException(t, e);
        if (this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(t, e);
        }
    }

    private void handleException(Thread t, Throwable e) {
        new Thread() {
            public void run() {
                Looper.prepare();
                try {
                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat *:e").getInputStream()));
                    while (true) {
                        line = br.readLine();
                        if (line == null) {
                            break;
                        }
                    }
                } catch (IOException e1) {
                }
                Looper.loop();
            }
        }.start();
    }
}
