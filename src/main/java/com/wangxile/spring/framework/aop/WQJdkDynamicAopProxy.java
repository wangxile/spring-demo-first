package com.wangxile.spring.framework.aop;

import com.wangxile.spring.framework.aop.interceptor.WQMethodInvocation;
import com.wangxile.spring.framework.aop.support.WQAdviceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 18:13
 */
public class WQJdkDynamicAopProxy implements WQAopProxy, InvocationHandler {

    private WQAdviceSupport adviceSupport;

    public WQJdkDynamicAopProxy(WQAdviceSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.adviceSupport.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,
                this.adviceSupport.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //将每一个 JoinPoint 也就是被代理的业务方法（Method ）封装成一个拦截器，组合成一个拦截器链
        List<Object> interceptorsAndDynamicMethodMatcherList = adviceSupport
                .getInterceptorsAndDynamicInterceptionAdvice(method, this.adviceSupport.getTargetClass());
        WQMethodInvocation invocation = new WQMethodInvocation(proxy,
                method, this.adviceSupport.getTarget(), this.adviceSupport.getTargetClass(), args,
                interceptorsAndDynamicMethodMatcherList);
        return invocation.proceed();
    }
}
