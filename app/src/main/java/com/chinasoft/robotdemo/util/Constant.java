package com.chinasoft.robotdemo.util;

import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.chinasoft.robotdemo.entity.Floor;

import java.util.ArrayList;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class Constant {
    public static String robotIp;
    public static int robotPort;
    public static String sdPath;

    public static String mapDirs="/sdcard/robotdemo/maps/";

    public static final String APP_VERSION = "ICS LBS Tester V200R002C17";
    public static final int COLLECT_DEFAULT_N = 15;
    public static final String COLLECT_DEFAULT_R = "30000";
    public static final int COLLECT_DEFAULT_T = 60;
    public static String IP_ADDRESS = "https://218.4.33.215:8083";
    public static String IP_MIXLOCATION = "https://49.4.0.220";
    public static final String IVAS_EXTRA_INFO_DEFAULT = "<key>YzNlNGU4MjMtOTk2Yy00NjMyLTk4ZTMtMjFiNWZjZjBjYmE1</key>\n<secret>N87Cg3hCiEydJYnie9GANZizj8Zrnp4rpUKEFVEBZq8</secret>\n<BASE_URL>http://10.1.236.95</BASE_URL>\n<URI>/api/b83f8a7c-829b-4f6a-86c6-919774bfd63a/HuaweiServer/locationRequest</URI>\n<IsGateEv>true</IsGateEv>\n<gateId>com.huawei.ivas.engine</gateId>\n<gateAppKey>QWREI0VXZypPV3hDZE9iWA==</gateAppKey>\n<gateUrl>http://apigw-beta.huawei.com/api/service/PingManuServer/locationRequest</gateUrl>\n";
    public static final String LICENSE_PRIFIX = "License:";
    public static final int MSG_COLLECT_DELAY = 2000;
    public static final int MSG_COLLECT_UPDATA_TASK = 1;
    public static final int MSG_COLLECT_UPLOAD_PROGRESS = 5;
    public static final int MSG_FOLLOWING_OVERTIME = 1;
    public static final int MSG_FOLLOWING_START_LOCATION = 0;
    public static final int MSG_LOCATION_DELAY = 1000;
    public static final int MSG_PING_CONNECTING = 0;
    public static final int MSG_PING_FAIL = 2;
    public static final int MSG_PING_SUCCESS = 1;
    public static final int REQUEST_CODE_HOME_TO_MAPSELECT = 100;
    public static final int REQUEST_CODE_HOME_TO_SETTING = 200;
    public static final int RESPONSE_COLLECT_FAIL = 402;
    public static final int RESPONSE_DELETE_FAIL = 404;
    public static final int RESPONSE_DOING = 401;
    public static final int RESPONSE_EXIST = 400;
    public static final int RESPONSE_LOCATION_ERROR = 100;
    public static final int RESPONSE_LOCATION_SUCCESS = 200;
    public static final int RESPONSE_SERVER_ERROR = 403;
    public static final int RESPONSE_SUCCESS = 200;
    public static final int STATIC_DEFAULT_N = 15;
    public static final int STATIC_DEFAULT_T = 67;
    public static String TOKEN_MIXLOCATION = "59922a7b77dbfacf3c86d64d022078c1";
    public static Floor currentFloor;
    public static InterRequestUtil interRequestUtil;
    public static RequestQueue mRequestQueue;
    public static ArrayList<Floor> mapData = new ArrayList();
    public static String path;
    public static Toast toast;
    public static String userId;
}
