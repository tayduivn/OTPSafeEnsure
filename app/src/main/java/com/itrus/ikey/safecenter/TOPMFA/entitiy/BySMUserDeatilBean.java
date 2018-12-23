package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * @Date 2018/8/30 下午7:13
 * @Author Jalen
 * @Email:c9n9m@163.com
 * @Description
 */
public class BySMUserDeatilBean {


    /**
     * status : 0x0000
     * message : 操作成功
     * data : {"time":1535627528371,"user":{"username":"gaomengqiu","nickname":"","name":"gaomengqiu","sex0":"女","phone":"18201237753","email":"mengqiu.gao@topca.cn","birth":"","hexSha1Photo":"","status":"0","logonFailedTime":0,"logonLockedTime":0,"lastUpdatePasswordTime":"2018-08-20 14:12:11.0","sourceId":0,"sourceType":"","sourceName":"","batchNumber":"","id":65564,"version":1,"itemCreateTime":"2018-08-20 14:12:11.0"}}
     */

    private String status;
    private String message;
    private DataBean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
         * time : 1535627528371
         * user : {"username":"gaomengqiu","nickname":"","name":"gaomengqiu","sex0":"女","phone":"18201237753","email":"mengqiu.gao@topca.cn","birth":"","hexSha1Photo":"","status":"0","logonFailedTime":0,"logonLockedTime":0,"lastUpdatePasswordTime":"2018-08-20 14:12:11.0","sourceId":0,"sourceType":"","sourceName":"","batchNumber":"","id":65564,"version":1,"itemCreateTime":"2018-08-20 14:12:11.0"}
         */

        private long time;
        private UserBean user;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * username : gaomengqiu
             * nickname :
             * name : gaomengqiu
             * sex0 : 女
             * phone : 18201237753
             * email : mengqiu.gao@topca.cn
             * birth :
             * hexSha1Photo :
             * status : 0
             * logonFailedTime : 0
             * logonLockedTime : 0
             * lastUpdatePasswordTime : 2018-08-20 14:12:11.0
             * sourceId : 0
             * sourceType :
             * sourceName :
             * batchNumber :
             * id : 65564
             * version : 1
             * itemCreateTime : 2018-08-20 14:12:11.0
             */

            private String username;
            private String nickname;
            private String name;
            private String sex0;
            private String phone;
            private String email;
            private String birth;
            private String hexSha1Photo;
            private String status;
            private int logonFailedTime;
            private int logonLockedTime;
            private String lastUpdatePasswordTime;
            private int sourceId;
            private String sourceType;
            private String sourceName;
            private String batchNumber;
            private int id;
            private int version;
            private String itemCreateTime;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSex0() {
                return sex0;
            }

            public void setSex0(String sex0) {
                this.sex0 = sex0;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getBirth() {
                return birth;
            }

            public void setBirth(String birth) {
                this.birth = birth;
            }

            public String getHexSha1Photo() {
                return hexSha1Photo;
            }

            public void setHexSha1Photo(String hexSha1Photo) {
                this.hexSha1Photo = hexSha1Photo;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getLogonFailedTime() {
                return logonFailedTime;
            }

            public void setLogonFailedTime(int logonFailedTime) {
                this.logonFailedTime = logonFailedTime;
            }

            public int getLogonLockedTime() {
                return logonLockedTime;
            }

            public void setLogonLockedTime(int logonLockedTime) {
                this.logonLockedTime = logonLockedTime;
            }

            public String getLastUpdatePasswordTime() {
                return lastUpdatePasswordTime;
            }

            public void setLastUpdatePasswordTime(String lastUpdatePasswordTime) {
                this.lastUpdatePasswordTime = lastUpdatePasswordTime;
            }

            public int getSourceId() {
                return sourceId;
            }

            public void setSourceId(int sourceId) {
                this.sourceId = sourceId;
            }

            public String getSourceType() {
                return sourceType;
            }

            public void setSourceType(String sourceType) {
                this.sourceType = sourceType;
            }

            public String getSourceName() {
                return sourceName;
            }

            public void setSourceName(String sourceName) {
                this.sourceName = sourceName;
            }

            public String getBatchNumber() {
                return batchNumber;
            }

            public void setBatchNumber(String batchNumber) {
                this.batchNumber = batchNumber;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public String getItemCreateTime() {
                return itemCreateTime;
            }

            public void setItemCreateTime(String itemCreateTime) {
                this.itemCreateTime = itemCreateTime;
            }
        }
    }
}
