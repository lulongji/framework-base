package com.lulj.base.cache.Local;

import java.io.Serializable;

public class Cache implements Serializable {

    private String value;
    private long expire = 0;

    public Cache(String value, long expire) {
        this.value = value;
        this.expire = System.currentTimeMillis() + expire * 1000;
    }

    public Cache(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}