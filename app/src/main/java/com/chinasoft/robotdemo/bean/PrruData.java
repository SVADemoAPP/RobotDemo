package com.chinasoft.robotdemo.bean;

/**
 * Created by XHF on 2018/12/5.
 */

public class PrruData {

    /**
     * code : 0
     * message : 成功
     * data : {"id":"7098","userId":"10.148.102.163","rsrp":-68,"timestamp":1544014397000}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 7098
         * userId : 10.148.102.163
         * rsrp : -68
         * timestamp : 1544014397000
         */

        private String id;
        private String userId;
        private int rsrp;
        private long timestamp;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getRsrp() {
            return rsrp;
        }

        public void setRsrp(int rsrp) {
            this.rsrp = rsrp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
