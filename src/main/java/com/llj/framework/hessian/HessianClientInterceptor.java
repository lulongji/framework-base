package com.llj.framework.hessian;


import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.SerializerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.ConnectException;
import java.net.MalformedURLException;

public class HessianClientInterceptor extends UrlBasedRemoteAccessor implements MethodInterceptor {
    private HessianProxyFactory proxyFactory = new HessianProxyFactory();
    private Object hessianProxy;

    public HessianClientInterceptor() {
    }

    public void setProxyFactory(HessianProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory != null?proxyFactory:new HessianProxyFactory();
    }

    public void setSerializerFactory(SerializerFactory serializerFactory) {
        this.proxyFactory.setSerializerFactory(serializerFactory);
    }

    public void setSendCollectionType(boolean sendCollectionType) {
        this.proxyFactory.getSerializerFactory().setSendCollectionType(sendCollectionType);
    }

    public void setUsername(String username) {
        this.proxyFactory.setUser(username);
    }

    public void setPassword(String password) {
        this.proxyFactory.setPassword(password);
    }

    public void setOverloadEnabled(boolean overloadEnabled) {
        this.proxyFactory.setOverloadEnabled(overloadEnabled);
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.prepare();
    }

    public void prepare() throws RemoteLookupFailureException {
        try {
            this.hessianProxy = this.createHessianProxy(this.proxyFactory);
        } catch (MalformedURLException var2) {
            throw new RemoteLookupFailureException("Service URL [" + this.getServiceUrl() + "] is invalid", var2);
        }
    }

    protected Object createHessianProxy(HessianProxyFactory proxyFactory) throws MalformedURLException {
        Assert.notNull(this.getServiceInterface(), "\'serviceInterface\' is required");
        return proxyFactory.create(this.getServiceInterface(), this.getServiceUrl());
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if(this.hessianProxy == null) {
            throw new IllegalStateException("HessianClientInterceptor is not properly initialized - invoke \'prepare\' before attempting any operations");
        } else {
            try {
                return invocation.getMethod().invoke(this.hessianProxy, invocation.getArguments());
            } catch (InvocationTargetException var5) {
                if(var5.getTargetException() instanceof HessianRuntimeException) {
                    HessianRuntimeException utex1 = (HessianRuntimeException)var5.getTargetException();
                    Object rootCause = utex1.getRootCause() != null?utex1.getRootCause():utex1;
                    throw this.convertHessianAccessException((Throwable)rootCause);
                } else if(var5.getTargetException() instanceof UndeclaredThrowableException) {
                    UndeclaredThrowableException utex = (UndeclaredThrowableException)var5.getTargetException();
                    throw this.convertHessianAccessException(utex.getUndeclaredThrowable());
                } else {
                    throw var5.getTargetException();
                }
            } catch (Throwable var6) {
                throw new RemoteProxyFailureException("Failed to invoke Hessian proxy for remote service [" + this.getServiceUrl() + "]", var6);
            }
        }
    }

    protected RemoteAccessException convertHessianAccessException(Throwable ex) {
        return (RemoteAccessException)(ex instanceof ConnectException?new RemoteConnectFailureException("Cannot connect to Hessian remote service at [" + this.getServiceUrl() + "]", ex):new RemoteAccessException("Cannot access Hessian remote service at [" + this.getServiceUrl() + "]", ex));
    }
}