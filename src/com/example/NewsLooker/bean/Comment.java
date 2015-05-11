package com.example.NewsLooker.bean;

import java.io.Serializable;

public class Comment implements Serializable{

    public int id;
    public int newsid;
    public int usersid;
    public String content;
    public String time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != comment.id) return false;
        if (newsid != comment.newsid) return false;
        if (usersid != comment.usersid) return false;
        if (!content.equals(comment.content)) return false;
        if (!time.equals(comment.time)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + newsid;
        result = 31 * result + usersid;
        result = 31 * result + content.hashCode();
        result = 31 * result + time.hashCode();
        return result;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewsid() {
        return newsid;
    }

    public void setNewsid(int newsid) {
        this.newsid = newsid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUsersid() {
        return usersid;
    }

    public void setUsersid(int usersid) {
        this.usersid = usersid;
    }
}
