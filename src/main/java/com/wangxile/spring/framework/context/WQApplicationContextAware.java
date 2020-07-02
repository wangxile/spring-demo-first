package com.wangxile.spring.framework.context;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/29 0029 19:29
 * <p>
 */
public interface WQApplicationContextAware {

    /**
     * 通过解糯方式获得 IoC 容器的顶层设计
     * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
     * 将自动调用 setApplicationContext() 方法，从而将 IoC 容器注入目标类中
     *
     * @param gpApplicationContext
     */
    void setApplicationContext(WQApplicationContext gpApplicationContext);
}
