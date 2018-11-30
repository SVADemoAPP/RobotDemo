package com.chinasoft.robotdemo.db.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chinasoft.robotdemo.db.AppDataBaseHelper;

/**
 * Sqlite数据库操作
 */
public class SqliteDao {
	public SQLiteDatabase writeDatabase;
	public SQLiteDatabase readDatabase;
	private static final String TABLE_NAME = "list_userId";
	private static final String SELECT_FOR_ALL = "select * from " + TABLE_NAME + " order by time desc";
	private static final String SELECT_BY_NAME = "select * from " + TABLE_NAME + " where name=?";
	private static final String UPDATESUBJECT_SQL = "update " + TABLE_NAME + " set time=? where name=?";
	private static final String DELETE_HISTORY_SQL = "delete from " + TABLE_NAME;

	private static SqliteDao searchDao;
	
	private SqliteDao(Context context) {
		writeDatabase = AppDataBaseHelper.getWriteAbleDateBase(context);
		readDatabase = AppDataBaseHelper.getReadAbleDateBase(context);

	}
	
	public static SqliteDao getInstance(Context context){
		if (searchDao == null) {
			searchDao = new SqliteDao(context);
			return searchDao;
		} else {
			return searchDao;
		}
	}

	/**
	 * 插入数据，以名称和时间戳
	 */
	public void insertHistory(String name, long time) {
		try {
			writeDatabase.beginTransaction();
			ContentValues cv = new ContentValues();
			cv.put("name", name);
			cv.put("time", time);
			updateList(cv, name, time);
			writeDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeDatabase.endTransaction();
		}
	}

	/**
	 * 更新数据表，如果已存在则更新，不存在则插入
	 */

	public void updateList(ContentValues cv, String name, long time) {
		boolean needUpdate = false;
		try {
			Cursor cursor = readDatabase.rawQuery(SELECT_BY_NAME,new String[]{name});
			try {
				while (cursor.moveToNext()) {
					writeDatabase.execSQL(UPDATESUBJECT_SQL, new Object[] { time, name });
					needUpdate = true;
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!needUpdate) {
			writeDatabase.insert(TABLE_NAME, null, cv);
		}
	}

	/**
	 * 获取所有数据
	 */

	public List<String> getAllName() {
		List<String> list = new ArrayList<String>();
		try {
			Cursor cursor = readDatabase.rawQuery(SELECT_FOR_ALL, null);
			try {
				while (cursor.moveToNext()) {
					list.add(cursor.getString(cursor.getColumnIndex("name")));
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 清空查询记录表
	 */

	public void clearAllSearch() {
		try {
			writeDatabase.beginTransaction();
			writeDatabase.execSQL(DELETE_HISTORY_SQL);
			writeDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeDatabase.endTransaction();
		}
	}

	/**
	 * 关闭
	 */
	public static void close(){
		if (searchDao != null) {
			searchDao = null;
		}
	}
	
}

