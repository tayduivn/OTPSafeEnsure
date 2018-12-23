package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * Created by STAR on 2016/8/29.
 */
public class PushMessage {

    /**
     * uuid : eadc8d15-7de3-4ec0-a5e6-faf2a84c7808
     * audience : 18646666742
     * type : auth
     * method : face
     * extensions :
     * subject : 这是测试消息头122
     * description : 这是测试消息内容1222
     */

    private String uuid;
    private String audience;
    private String type;
    private String method;
    private String extensions;
    private String subject;
    private String description;
    private String exclude;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }


}
