package com.chinasoft.robotdemo.db.dbflow;

import com.chinasoft.robotdemo.framwork.entity.BaseModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by XHF on 2018/12/4.
 */

@Table(database = AppDataBase.class)
public class DirectionData extends BaseModel{
//    @PrimaryKey(autoincrement = true)    //ID
//    public long id;
    @Column
    public String mapName;

    @Column
    public String directionName;

    @Column
    public String path;
}
