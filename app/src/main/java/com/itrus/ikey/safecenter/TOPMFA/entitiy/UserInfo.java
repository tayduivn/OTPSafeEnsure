package com.itrus.ikey.safecenter.TOPMFA.entitiy;

/**
 * Created by STAR on 2016/8/13.
 */
public class UserInfo {

    private int id;
    private String username;
    private String nickname;
    private String name;
    private String sex0;
    private String phone;
    private String email;
    private String birth;
    private String hexSha1Photo;
    private String status;
    private String certificationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(String certificationId) {
        this.certificationId = certificationId;
    }

}
