package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * Created by STAR on 2016/8/31.
 */
public class EventBusMessage {

    int state;

    private String message;

    private String signData;

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
