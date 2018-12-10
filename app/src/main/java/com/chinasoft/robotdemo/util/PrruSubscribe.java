package com.chinasoft.robotdemo.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;

import org.json.JSONObject;

public class PrruSubscribe {
    private Context context;

    public PrruSubscribe(Context context) {
        this.context = context;
    }

    public void toSubscription() {
        JsonObjectPostRequest newMissRequest = new JsonObjectPostRequest(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/subscribePrru?storeId=" + Constant.storeId + "&ip=" + Constant.userId, new Listener<JSONObject>() {
            public void onResponse(JSONObject jsonobj) {
                LLog.getLog().e("prru订阅结果:", jsonobj.toString());
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                LLog.getLog().e("prru订阅失败:", error.toString());
            }
        });
        newMissRequest.setSendCookie(SharedPrefHelper.getString(context, "Cookie"));
        Constant.mRequestQueue.add(newMissRequest);
    }

    public void cancleSubscription() {
        JsonObjectPostRequest newMissRequest = new JsonObjectPostRequest(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/unSubscribePrru?storeId=" + Constant.storeId + "&ip=" + Constant.userId, new Listener<JSONObject>() {
            public void onResponse(JSONObject jsonobj) {
                LLog.getLog().e("prru取消订阅成功:", jsonobj.toString());
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                LLog.getLog().e("prru取消订阅失败:", error.toString());
            }
        });
        newMissRequest.setSendCookie(SharedPrefHelper.getString(context, "Cookie"));
        Constant.mRequestQueue.add(newMissRequest);
    }
}
