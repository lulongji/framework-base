package com.llj.framework.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

public class QDHessianProxyFactory extends HessianProxyFactory {

    @Override
    public Object create(Class api, String urlName, ClassLoader loader) throws MalformedURLException {
        if(api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        } else {
            QDHessianProxy handler = null;
            URL url = new URL(urlName);
            handler = new QDHessianProxy(this,url);
            return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, handler);
        }
    }
}

