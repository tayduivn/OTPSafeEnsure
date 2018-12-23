package com.itrus.ikey.safecenter.TOPMFA.entitiy;

import java.util.List;

public class FootDetailBean {


    /**
     * status : 0x0000
     * recordList : [{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-09 11:57:39.0","year":"本月","month":"今天","hour":"11:57","weekOfDays":"星期四"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-09 11:45:38.0","year":"","month":"今天","hour":"11:45","weekOfDays":"星期四"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-09 09:42:10.0","year":"","month":"今天","hour":"09:42","weekOfDays":"星期四"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"222.128.178.104","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-09 08:54:21.0","year":"","month":"今天","hour":"08:54","weekOfDays":"星期四"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 18:05:34.0","year":"","month":"08-08","hour":"18:05","weekOfDays":"星期三"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 16:21:46.0","year":"","month":"08-08","hour":"16:21","weekOfDays":"星期三"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 16:21:12.0","year":"","month":"08-08","hour":"16:21","weekOfDays":"星期三"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 16:17:49.0","year":"","month":"08-08","hour":"16:17","weekOfDays":"星期三"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 15:47:59.0","year":"","month":"08-08","hour":"15:47","weekOfDays":"星期三"},{"username":"gaomengqiu","name":"4","todo":"平台登录","host":"121.69.6.113","appName":"Hawk","result":"success","mode":"平台登录","description":"","time":"2018-08-08 15:47:01.0","year":"","month":"08-08","hour":"15:47","weekOfDays":"星期三"}]
     * recordCount : 10
     */

    private String status;
    private int recordCount;
    private List<RecordListBean> recordList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<RecordListBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordListBean> recordList) {
        this.recordList = recordList;
    }

    public static class RecordListBean {
        /**
         * username : gaomengqiu
         * name : 4
         * todo : 平台登录
         * host : 121.69.6.113
         * appName : Hawk
         * result : success
         * mode : 平台登录
         * description :
         * time : 2018-08-09 11:57:39.0
         * year : 本月
         * month : 今天
         * hour : 11:57
         * weekOfDays : 星期四
         */

        private String username;
        private String name;
        private String todo;
        private String host;
        private String appName;
        private String result;
        private String mode;
        private String description;
        private String time;
        private String year;
        private String month;
        private String hour;
        private String weekOfDays;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTodo() {
            return todo;
        }

        public void setTodo(String todo) {
            this.todo = todo;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getWeekOfDays() {
            return weekOfDays;
        }

        public void setWeekOfDays(String weekOfDays) {
            this.weekOfDays = weekOfDays;
        }
    }
}
