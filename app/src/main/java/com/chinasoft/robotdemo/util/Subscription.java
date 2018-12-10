package com.chinasoft.robotdemo.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.chinasoft.robotdemo.entity.Floor;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Subscription {
    private Context context;
    private RequestQueue mRequestQueue;

    public Subscription(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void toSubscription() {
        JsonObjectPostRequest newMissRequest = new JsonObjectPostRequest(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/subscription?storeId=" + Constant.storeId + "&ip=" + Constant.userId, new Listener<JSONObject>() {
            public void onResponse(JSONObject jsonobj) {
                LLog.getLog().e("指定用户订阅结果", jsonobj.toString());
                try {
                    String status = jsonobj.getString("status");
                    int msg = Integer.parseInt(jsonobj.getString("message"));
                    if ("failed".equals(status)) {
                        switch (msg) {
                            case 300:
                                Toast.makeText(context, "连接被拒绝,获取token端口不对或SSL版本不一致", Toast.LENGTH_SHORT).show();
                                return;
                            case 400:
                                Toast.makeText(context, "参数错误,app来回切换订阅类型导致订阅出错或者ID类型不支持", Toast.LENGTH_SHORT).show();
                                return;
                            case 401:
                                Toast.makeText(context, "app密码或者账号有误", Toast.LENGTH_SHORT).show();
                                return;
                            case 403:
                                Toast.makeText(context, "连接超时,网络问题或者未加路由或者ip不对", Toast.LENGTH_SHORT).show();
                                return;
                            case 404:
                                Toast.makeText(context, "未找到SVA配置信息", Toast.LENGTH_SHORT).show();
                                return;
                            case 500:
                                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                return;
                            default:
                                return;
                        }
                    } else if ("succees".equals(status) && msg == 200) {
                        Toast.makeText(context, "订阅成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                LLog.getLog().e("指定用户订阅失败", error.toString());
                Toast.makeText(context, "连接超时,网络问题或者未加路由或者ip不对", Toast.LENGTH_SHORT).show();
            }
        });
        newMissRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1.0f));
        newMissRequest.setSendCookie(SharedPrefHelper.getString(context, "Cookie"));
        mRequestQueue.add(newMissRequest);
    }

    public void cancleSubscription(Floor currentFloor) {
        if (currentFloor != null) {
            JsonObjectPostRequest newMissRequest = new JsonObjectPostRequest(Request.Method.POST, Constant.IP_ADDRESS + "/tester/api/app/unsubscribe?storeId=" + Constant.storeId + "&ip=" + Constant.userId, new Listener<JSONObject>() {
                public void onResponse(JSONObject jsonobj) {
                    Log.d("指定用户取消订阅成功:", jsonobj.toString());
                }
            }, new ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    LLog.getLog().e("指定用户取消订阅失败:", error.toString());
                }
            });
            newMissRequest.setSendCookie(SharedPrefHelper.getString(context, "Cookie"));
            mRequestQueue.add(newMissRequest);
        }
    }
}
