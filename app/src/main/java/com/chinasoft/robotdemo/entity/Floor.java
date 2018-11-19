package com.chinasoft.robotdemo.entity;

import java.io.Serializable;

public class Floor implements Serializable {
    private float angle;
    private String coordinate;
    private String floor;
    private String id;
    private int imgHeight;
    private int imgWidth;
    private String mapId;
    private String path;
    private String place;
    private float scale;
    private int siteId;
    private String updateTime;
    private float xo;
    private float yo;

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getXo() {
        return this.xo;
    }

    public void setXo(float xo) {
        this.xo = xo;
    }

    public float getYo() {
        return this.yo;
    }

    public void setYo(float yo) {
        this.yo = yo;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCoordinate() {
        return this.coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getImgWidth() {
        return this.imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return this.imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getSiteId() {
        return this.siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getMapId() {
        return this.mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String toString() {
        return "Floor [scale=" + this.scale + ", xo=" + this.xo + ", yo=" + this.yo + ", floor=" + this.floor + ", coordinate=" + this.coordinate + ", angle=" + this.angle + ", path=" + this.path + ", imgWidth=" + this.imgWidth + ", imgHeight=" + this.imgHeight + ", siteId=" + this.siteId + ", place=" + this.place + ", id=" + this.id + ", updateTime=" + this.updateTime + ", mapId=" + this.mapId + "]";
    }
}
