package com.chinasoft.robotdemo.db.dbflow;

import com.chinasoft.robotdemo.framwork.entity.BaseModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by XHF on 2018/12/4.
 */

@Table(database = AppDataBase.class)
public class DirectionData extends BaseModel {
    @PrimaryKey(autoincrement = true)    //ID
    public long id;
    @Column
    public String mapName;

    @Column
    public String directionName;

    @Column
    public String path;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DirectionData() {

    }

    public DirectionData(String mapName, String directionName, String path) {
        this.mapName = mapName;
        this.directionName = directionName;
        this.path = path;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "DirectionData{" +
                "id=" + id +
                ", mapName='" + mapName + '\'' +
                ", directionName='" + directionName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }


}
