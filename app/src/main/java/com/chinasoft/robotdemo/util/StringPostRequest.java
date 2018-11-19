package com.chinasoft.robotdemo.util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StringPostRequest extends Request<String> {
    public String cookieFromResponse;
    private String mHeader;
    private Listener<String> mListener;
    private Map<String, String> mMap;
    private Map<String, String> sendHeader = new HashMap(1);

    public StringPostRequest(int method, String url, Listener<String> listener, ErrorListener errorListener, Map map) {
        super(method, url, errorListener);
        this.mListener = listener;
        mMap = map;
    }

    public StringPostRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return mMap;
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            this.cookieFromResponse = (String) response.headers.get("Set-Cookie");
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            this.mHeader = response.headers.toString();
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Throwable e) {
            return Response.error(new ParseError(e));
        }
    }

    protected void deliverResponse(String response) {
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
