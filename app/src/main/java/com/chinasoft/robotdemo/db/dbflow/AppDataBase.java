package com.chinasoft.robotdemo.db.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by XHF on 2018/12/4.
 */

@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public final class AppDataBase {
    //数据库名称
    public static final String NAME = "AppDatabase";
    //数据库版本号
    public static final int VERSION = 1;

}


