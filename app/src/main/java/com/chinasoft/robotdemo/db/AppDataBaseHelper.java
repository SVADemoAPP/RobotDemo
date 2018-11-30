package com.chinasoft.robotdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Sqlite数据库帮助类
 */
public class AppDataBaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "robot.db";

	private static final String TABLE_NAME = "list_userId";
	/**
	 * 版本号
	 */
	private static final int DB_VERSION = 2;

	private AppDataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	private static AppDataBaseHelper dbOpenHelper = null;

	/**
	 *  单例模式创建数据库帮助类
	 */
	public static synchronized AppDataBaseHelper getInstance(Context context) {
		if (dbOpenHelper == null) {
			dbOpenHelper = new AppDataBaseHelper(context);
			return dbOpenHelper;
		} else {
			return dbOpenHelper;
		}
	}
	
	/**
	 *
	 */
	public static SQLiteDatabase getWriteAbleDateBase(Context context){
		getInstance(context);
		return dbOpenHelper.getWritableDatabase();
	}
	/**
	 *
	 */
	public static SQLiteDatabase getReadAbleDateBase(Context context){
		getInstance(context);
		return dbOpenHelper.getReadableDatabase();
	}

	/**
	 *
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createDbTable(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		
		super.onOpen(db);
	}

	/**
	 * 建表
	 */
	private void createDbTable(SQLiteDatabase db) {
		/**
		 * 建表语句的Sql注入
		 */
		db.execSQL("create table if not exists " + TABLE_NAME + "(id INTEGER PRIMARY KEY autoincrement,name TEXT,time long)");
	}

	/**
	 * 版本升级
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
