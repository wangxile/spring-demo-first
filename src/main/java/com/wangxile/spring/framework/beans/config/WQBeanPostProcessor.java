package com.wangxile.spring.framework.beans.config;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/29 0029 20:12
 * <p>
 * BeanFactoryPostProcessor，是针对整个工厂生产出来的BeanDefinition作出修改或者注册。作用于BeanDefinition时期。
 * <p>
 * BeanPostProcessor 的作用于bean实例化、初始化前后执行
 */
public class WQBeanPostProcessor {

    /**
     * bean初始化之前提供回调
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * bean初始化之后提供回调
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

}
