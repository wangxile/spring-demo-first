package com.wangxile.spring.framework.context.support;

import com.wangxile.spring.framework.beans.config.WQBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/23 0023 20:56
 * <p>
 * DefaultListableBeanFactory是众多IoC容器子类的典型代表。在Mini版本中我只做了一个简
 * 单的设计，就是定义顶层的 IoC 缓存 ，也就是 Map ，属性名也和原生 Spring 保持一致，定
 * 义为 beanDefinitionMap 以方便大家对比理解。
 */
public class WQDefaultlistableBeanFactory extends WQAbstractApplicationContext {
    /**
     * 存储注册信息的bean
     */
    protected final Map<String, WQBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, WQBeanDefinition>();
}
