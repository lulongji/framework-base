package com.llj.framework.hessian;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

@SuppressWarnings("rawtypes")
public class HessianProxyFactoryBean extends HessianClientInterceptor implements FactoryBean, BeanClassLoaderAware {
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	private Object serviceProxy;

	public HessianProxyFactoryBean() {
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
    public void afterPropertiesSet() {
		super.afterPropertiesSet();
		this.serviceProxy = (new ProxyFactory(this.getServiceInterface(), this)).getProxy(this.beanClassLoader);
	}

	public Object getObject() {
		return this.serviceProxy;
	}

	public Class getObjectType() {
		return this.getServiceInterface();
	}

	public boolean isSingleton() {
		return true;
	}
}
