package com.chinasoft.robotdemo.bean;

public class PrruInfo {

    /**
     * 坐标点
     */
    private Position position;

    /**
     * 下行RSRP值
     */
    private int rsrp;

    /**
     * pRUU标识，默认-1
     */
    private int pRRUIndex;

    /**
     * 斜率
     */
    private double slope;

    /**
     * 夹角，默认-1，只有两条线路交接的两个点有值
     */
    private double includedAngle;

    /**
     * 线路标识，默认-1
     */
    private int routeId;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getpRRUIndex() {
        return pRRUIndex;
    }

    public void setpRRUIndex(int pRRUIndex) {
        this.pRRUIndex = pRRUIndex;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getIncludedAngle() {
        return includedAngle;
    }

    public void setIncludedAngle(double includedAngle) {
        this.includedAngle = includedAngle;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "PrruInfo{" +
                "position=" + position +
                ", rsrp=" + rsrp +
                ", pRRUIndex=" + pRRUIndex +
                ", slope=" + slope +
                ", includedAngle=" + includedAngle +
                ", routeId=" + routeId +
                '}';
    }
}
