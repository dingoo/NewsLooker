package com.example.NewsLooker.bean;

import java.io.Serializable;

public class NewsInfo implements Serializable{

    public int newid;
    public String title;
    public String type;
    public String time;
    public String desc;
    public String content_url;
    public String pic_url;
    public String from;
    public int hitsnum;
    public boolean readstatus=false;

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getHitsnum() {
        return hitsnum;
    }

    public void setHitsnum(int hitsnum) {
        this.hitsnum = hitsnum;
    }

    public int getNewid() {
        return newid;
    }

    public void setNewid(int newid) {
        this.newid = newid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public boolean getReadstatus(){
        return readstatus;
    }

    public void setReadstatus(boolean readstatus){
        this.readstatus=readstatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsInfo newsInfo = (NewsInfo) o;

        if (hitsnum != newsInfo.hitsnum) return false;
        if (newid != newsInfo.newid) return false;
        if (!content_url.equals(newsInfo.content_url)) return false;
        if (!desc.equals(newsInfo.desc)) return false;
        if (!from.equals(newsInfo.from)) return false;
        if (!pic_url.equals(newsInfo.pic_url)) return false;
        if (!time.equals(newsInfo.time)) return false;
        if (!title.equals(newsInfo.title)) return false;
        if (!type.equals(newsInfo.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = newid;
        result = 31 * result + title.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + desc.hashCode();
        result = 31 * result + content_url.hashCode();
        result = 31 * result + pic_url.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + hitsnum;
        return result;
    }
}
