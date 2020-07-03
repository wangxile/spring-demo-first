package com.wangxile.spring.framework.aop;

import com.wangxile.spring.framework.aop.support.WQAdviceSupport;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 18:10
 */
public class WQCglibAopProxy implements WQAopProxy {

    private WQAdviceSupport adviceSupport;

    public WQCglibAopProxy(WQAdviceSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classloader) {
        return null;
    }
}
