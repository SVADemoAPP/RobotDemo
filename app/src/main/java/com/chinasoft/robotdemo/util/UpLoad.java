package com.chinasoft.robotdemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import org.json.JSONObject;

public class UpLoad {
    private String TAG;
    private Context context;
    private NetworkInfo info;
    private String lastIP;
    private ConnectivityManager mConnectivityManager;
    private RequestQueue mRequestQueue;
    private int placeId = 0;
    private WifiManager wifiManager;

    @SuppressLint("WrongConstant")
    public UpLoad(Context context) {
        this.context = context;
        mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        wifiManager = (WifiManager) context.getSystemService("wifi");
        mRequestQueue = Volley.newRequestQueue(context);
        TAG = context.getPackageName();
    }

    public String getLocaIpOrMac() {
        if ("MAC".equals(Constant.identification)) {
            return getLocaMAC();
        }
        String ip = getLocaIp();
        if (lastIP == null) {
            lastIP = ip;
        }
        if (!(ip == null || ip.equals(lastIP))) {
            toSubscription(ip);
        }
        lastIP = ip;
        LLog.getLog().e("---ip地址---", ip);
        return ip;
    }

    public String setIpPassword() {
        String ip = getLocaIp();
        if (ip == null) {
            return null;
        }
        String[] spliteIp = ip.split("\\.");
        String firstIP = "";
        if (spliteIp.length < 4) {
            return null;
        }
        int i;
        for (i = 0; i < spliteIp[0].length(); i++) {
            firstIP = new StringBuilder(String.valueOf(firstIP)).append("*").toString();
        }
        String secondIP = "";
        for (i = 0; i < spliteIp[1].length(); i++) {
            secondIP = new StringBuilder(String.valueOf(secondIP)).append("*").toString();
        }
        return new StringBuilder(String.valueOf(firstIP)).append(".").append(secondIP).append(".").append(spliteIp[2]).append(".").append(spliteIp[3]).toString();
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    private void toSubscription(String ip) {
        Log.i("placeId", new StringBuilder(String.valueOf(this.placeId)).toString());
        mRequestQueue.add(new JsonObjectRequest(Request.Method.POST, Constant.IP_ADDRESS + "/sva/api/subscription?storeId=" + placeId + "&ip=" + ip, new JSONObject(new HashMap()), new Listener<JSONObject>() {
            public void onResponse(JSONObject jsonobj) {
                Log.i("subscription", jsonobj.toString());
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                LLog.getLog().e(TAG, "jsonobj:" + error.toString());
            }
        }));
    }

    private String getLocaMAC() {
        LLog.getLog().e("开始获取mac:", ">>>");
        info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            LLog.getLog().e("网络不可用，返回默认mac", "-4C:4C:4C:4C:4C:4C-");
            return "4C:4C:4C:4C:4C:4C";
        } else if (info.getType() == 0) {
            LLog.getLog().e("网络类型:", "TYPE_MOBILE");
            return getLocalMacAddressFromIp();
        } else if (1 == info.getType()) {
            LLog.getLog().e("网络类型:", "TYPE_WIFI");
            return wifiManager.getConnectionInfo().getMacAddress();
        } else {
            LLog.getLog().e("网络类型:", new StringBuilder(String.valueOf(info.getTypeName())).append("-返回默认mac-4C:4C:4C:4C:4C:4C-").toString());
            return "4C:4C:4C:4C:4C:4C";
        }
    }

    public String getLocalMacAddressFromIp() {
        String mac_s = "";
        try {
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            if (ne != null) {
                mac_s = byte2hex(ne.getHardwareAddress());
            }
        } catch (Exception e) {
            LLog.getLog().e("exception", e.toString());
        }
        return mac_s.toUpperCase();
    }

    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                if (n == 0) {
                    hs = hs.append("0").append(stmp);
                } else {
                    hs = hs.append("-").append("0").append(stmp);
                }
            } else if (n == 0) {
                hs = hs.append(stmp);
            } else {
                hs = hs.append("-").append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    private String getLocaIp() {
        LLog.getLog().e("开始获取ip:", ">>>");
        info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            LLog.getLog().e("网络不可用,", ">返回默认ip 192.168.0.1>");
            return "192.168.0.1";
        } else if (info.getType() == 0) {
            LLog.getLog().e("网络类型,TYPE_MOBILE", ">>>");
            return getLocalIpAddress();
        } else if (1 == info.getType()) {
            LLog.getLog().e("网络类型,TYPE_WIFI", ">>>");
            return intToIp(wifiManager.getConnectionInfo().getIpAddress());
        } else {
            LLog.getLog().e("网络类型," + info.getTypeName(), ">返回默认ip 192.168.0.1>");
            return "192.168.0.1";
        }
    }

    private String intToIp(int i) {
        return (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !intf.toString().contains("wlan")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            LLog.getLog().e(TAG, ex.toString());
        }
        return null;
    }
}
