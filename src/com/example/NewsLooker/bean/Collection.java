package com.example.NewsLooker.bean;

import java.io.Serializable;

public class Collection implements Serializable{

    public int id;
    public int nid;
    public int uid;
    public int is_collect=0;//0£∫Œ¥ ’≤ÿ  1£∫ ’≤ÿ

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(int is_collect) {
        this.is_collect = is_collect;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (id != that.id) return false;
        if (nid != that.nid) return false;
        if (uid != that.uid) return false;
        return is_collect == that.is_collect;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + nid;
        result = 31 * result + uid;
        result = 31 * result + is_collect;
        return result;
    }
}
