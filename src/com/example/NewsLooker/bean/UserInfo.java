package com.example.NewsLooker.bean;

import java.io.Serializable;

public class UserInfo implements Serializable{

    public int userid;
    public String username;
    public String password;
    public String usericon;
    public String nickname;
    public String city;
    public String phonenum;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getUsericon() {
        return usericon;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (userid != userInfo.userid) return false;
        if (!city.equals(userInfo.city)) return false;
        if (!nickname.equals(userInfo.nickname)) return false;
        if (!password.equals(userInfo.password)) return false;
        if (!phonenum.equals(userInfo.phonenum)) return false;
        if (!usericon.equals(userInfo.usericon)) return false;
        if (!username.equals(userInfo.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userid;
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + usericon.hashCode();
        result = 31 * result + nickname.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + phonenum.hashCode();
        return result;
    }
}
