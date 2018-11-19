package com.chinasoft.robotdemo.util;

import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;

public class JsonObjectPostRequest extends JsonObjectRequest {
    public String cookieFromResponse;
    private String mHeader;
    private Listener<JSONObject> mListener;
    private Map<String, String> sendHeader = new HashMap(1);

    public JsonObjectPostRequest(int method, String url, Listener<JSONObject> listener, ErrorListener errorListener, Map map) {
        super(method, url, new JSONObject(map), listener, errorListener);
        this.mListener = listener;
    }

    public JsonObjectPostRequest(int method, String url, Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.mListener = listener;
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            this.cookieFromResponse = (String) response.headers.get("Set-Cookie");
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            this.mHeader = response.headers.toString();
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject.put("Cookie", this.cookieFromResponse);
            Log.w("LOG", "jsonObject " + jsonObject.toString());
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Throwable e) {
            return Response.error(new ParseError(e));
        } 
    }

    protected void deliverResponse(JSONObject response) {
        this.mListener.onResponse(response);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return this.sendHeader;
    }

    public void setSendCookie(String cookie, String session) {
        this.sendHeader.put("Set-Cookie", cookie);
        this.sendHeader.put("Session", session);
    }

    public void setSendCookie(String cookie) {
        this.sendHeader.put("Cookie", cookie);
    }

    public void addSession(Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            this.sendHeader.put((String) entry.getKey(), (String) entry.getValue());
        }
    }
}
