package com.chinasoft.robotdemo.bean;

public class MaxrsrpPosition {
    private float x;
    private float y;
    private float rsrp;
    public MaxrsrpPosition() {
    }

    public MaxrsrpPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRsrp() {
        return rsrp;
    }

    public void setRsrp(float rsrp) {
        this.rsrp = rsrp;
    }

    @Override
    public String toString() {
        return "MaxrsrpPosition{" +
                "x=" + x +
                ", y=" + y +
                ", rsrp=" + rsrp +
                '}';
    }
}
