package com.wangxile.spring.framework.context.support;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/23 0023 20:53
 * <p>
 * IoC 容器实现类的顶层抽象类 实现IOC容器相关的公共逻辑。为了尽可能地简化，在这
 * Mini版本中， 暂时只设计了 一个 refresh （） 方法。
 */
public abstract class WQAbstractApplicationContext {

    /**
     * 受保护只提供给子类重写
     *
     * @throws Exception
     */
    public void refresh() throws Exception {
    }
}
