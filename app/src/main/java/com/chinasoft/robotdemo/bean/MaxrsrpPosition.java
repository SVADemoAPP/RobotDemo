package com.chinasoft.robotdemo.bean;

public class MaxrsrpPosition {
    private float x;
    private float y;
    private float rsrp;
    private int num;

    public MaxrsrpPosition() {
    }

    public MaxrsrpPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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
                ", num=" + num +
                '}';
    }
}
