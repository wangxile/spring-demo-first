package com.wangxile.spring.framework.aop;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 18:06
 * <p>
 * GPAopProxy 代理工厂的顶层接口， 其子类主要有两个： CglibAopProxy JdkDynamicAopProxy
 */
public interface WQAopProxy {

    /**
     * 获得代理对象
     *
     * @return
     */
    Object getProxy();


    /**
     * 通过自定义类加载器获取代理对象
     *
     * @param classloader
     * @return
     */
    Object getProxy(ClassLoader classloader);
}
