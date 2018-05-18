package com.llj.framework.hessian;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by qd on 2016/5/31.
 */
public class QDHessianHeader implements Serializable{

    private static final long serialVersionUID = 3769212736981422666L;

    private Map<String, String> headers = new ConcurrentHashMap<>();

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
