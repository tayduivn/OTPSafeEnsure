package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * Created by STAR on 2016/8/25.
 */
public class LoginDevices {
    private int loginType;      //0 电脑  1 手机
    private String loginTime;
    private String loginDevice;
    private String loginPos;

    public String getLoginPos() {
        return loginPos;
    }

    public void setLoginPos(String loginPos) {
        this.loginPos = loginPos;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }

}
