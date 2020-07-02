package com.wangxile.spring.framework.core;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/18 0018 20:42
 */
public interface WQBeanFactory {

    /**
     * 根据名称获取bean
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 根据类型获取bean
     *
     * @param beanClass
     * @return
     * @throws Exception
     */
    Object getBean(Class<?> beanClass) throws Exception;
}
