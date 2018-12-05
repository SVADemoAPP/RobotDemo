package com.chinasoft.robotdemo.bean;

public class RouteModel {
    private   String routeName;
    private   String routeJson;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteJson() {
        return routeJson;
    }

    public void setRouteJson(String routeJson) {
        this.routeJson = routeJson;
    }

    public RouteModel(String routeName, String routeJson) {
        this.routeName = routeName;
        this.routeJson = routeJson;
    }
}
