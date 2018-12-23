package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * Created by STAR on 2016/8/8.
 */
public class Info1 {

    /**
     * serial : 123456789
     * os : {"type":"小米","version":"MI2S"}
     * app : {"version":"MIUI527845"}
     * position : {"x":"123","y":"456"}
     */

    private String serial;
    /**
     * type : 小米
     * version : MS2I
     */

    private OsBean os;
    /**
     * version : MS2I
     */

    private AppBean app;
    /**
     * x : 123
     * y : 456
     */

    private PositionBean position;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public OsBean getOs() {
        return os;
    }

    public void setOs(OsBean os) {
        this.os = os;
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public PositionBean getPosition() {
        return position;
    }

    public void setPosition(PositionBean position) {
        this.position = position;
    }

    public static class OsBean {
        private String type;
        private String version;
        private String phone;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class AppBean {
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class PositionBean {
        private String x;
        private String y;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }
    }
}

