package com.chinasoft.robotdemo.util;

import android.support.annotation.NonNull;

import com.chinasoft.robotdemo.db.dbflow.DirectionData;
import com.chinasoft.robotdemo.db.dbflow.DirectionData_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.AsyncQuery;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XHF on 2018/12/5.
 */

public class DBUtils {
    public static ModelAdapter<DirectionData> adapter = FlowManager.getModelAdapter(DirectionData.class);

    /**
     * 新增
     *
     * @param data
     * @return
     */
    public static long insert(DirectionData data) {
        long insert = adapter.insert(data);
        return insert;
    }

    /**
     * 修改，必须指定ids
     *
     * @param data
     * @return
     */
    public static boolean update(DirectionData data) {
        return adapter.update(data);
    }

    /**
     * 删除
     *
     * @param data
     * @return
     */
    public static boolean delete(DirectionData data) {
        return adapter.delete(data);
    }

    /**
     * 暂时没用 样例
     *
     * @param mapName
     * @param directionName
     * @return
     */
    public static List<DirectionData> queryAsync(String mapName, String directionName) {
        final List<DirectionData> data = new ArrayList<>();
        OperatorGroup op = OperatorGroup.clause().and(DirectionData_Table.mapName.eq(mapName)).and(DirectionData_Table.directionName.eq(directionName));  //连接多个查询条件
        SQLite.select().from(DirectionData.class).where(op).async().queryListResultCallback(new QueryTransaction.QueryResultListCallback<DirectionData>() {   //执行异步查询
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<DirectionData> tResult) {
            }
        });
        return data;
    }

    /***
     * 使用同步返回查询数据
     * @param mapName
     * @param directionName
     * @return
     */
    public static List<DirectionData> query(String mapName, String directionName) {
        List<DirectionData> data;
        OperatorGroup op = OperatorGroup.clause().and(DirectionData_Table.mapName.eq(mapName)).and(DirectionData_Table.directionName.eq(directionName));  //连接多个查询条件
        data = SQLite.select().from(DirectionData.class).where(op).queryList();
        return data;
    }
}
