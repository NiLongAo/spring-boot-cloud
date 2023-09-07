/**
 * Copyright (c) 2005-2011 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * $Id: SpringContextHolder.java 1468 2011-02-10 15:22:48Z calvinxiu $
 */
package cn.com.tzy.springbootcomm.spring;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 *
 * @author calvin
 */
@Log4j2
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
        if(applicationContext == null) {
            throw new NullPointerException();
        }

		log.debug("注入ApplicationContext到SpringContextHolder:" + applicationContext);

		if (SpringContextHolder.applicationContext != null) {
			log.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextHolder.applicationContext);
		}

		SpringContextHolder.applicationContext = applicationContext; //NOSONAR
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		log.debug("destroy()");
		SpringContextHolder.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	public static void assertContextInjected() {
		AssertUtils.state(applicationContext != null,
                "applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
	}

	/**
	 * 动态注入单例bean实例
	 *
	 * @param beanName        bean名称
	 * @param singletonObject 单例bean实例
	 * @return 注入实例
	 */
	public static Object registerSingletonBean(String beanName, Object singletonObject) {




		//将applicationContext转换为ConfigurableApplicationContext
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		//获取BeanFactory
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
		//动态注册bean.
		defaultListableBeanFactory.registerSingleton(beanName, singletonObject);
		//获取动态注册的bean.
		return configurableApplicationContext.getBean(beanName);
	}
}
