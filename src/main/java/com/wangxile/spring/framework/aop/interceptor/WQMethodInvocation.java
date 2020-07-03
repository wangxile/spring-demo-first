package com.wangxile.spring.framework.aop.interceptor;

import com.wangxile.spring.framework.aop.aspect.WQJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 18:27
 * <p>
 * 执行拦截器链，相当于 Spring ReflectiveMethodInvocation 的功能
 */
public class WQMethodInvocation implements WQJoinPoint {

    /**
     * 代理对象
     */
    private Object proxy;

    /**
     * 代理的目标方法
     */
    private Method method;

    /**
     * 代理的目标对象
     */
    private Object target;

    /**
     * 代理的目标类
     */
    private Class<?> targetClass;

    /**
     * 代理的方法参数列表
     */
    private Object[] arguments;

    /**
     * 回调方法链
     */
    private List<Object> interceptorsAndDynamicMethodMatcherList;

    /**
     * 保存自定义属性
     */
    private Map<String, Object> userAttributes;

    private int currentInterceptorIndex = -1;

    public WQMethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments, List<Object> interceptorsAndDynamicMethodMatcherList) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatcherList = interceptorsAndDynamicMethodMatcherList;
    }

    /**
     * 执行拦截链
     *
     * @return
     * @throws Throwable
     */
    public Object proceed() throws Throwable {
        //如果 Interceptor 执行完了，则执行joinPoint
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatcherList.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }

        //执行拦截链
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatcherList
                .get(++currentInterceptorIndex);

        //如果要动态匹配 joinPoint
        if (interceptorOrInterceptionAdvice instanceof WQMethodInterceptor) {
            WQMethodInterceptor methodInterceptor = (WQMethodInterceptor) interceptorOrInterceptionAdvice;
            return methodInterceptor.invoke(this);
        } else {
            //执行当前 Intercetpor
            return proceed();
        }
    }

    public WQMethodInvocation() {
        super();
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<String, Object>();
            }
            this.userAttributes.put(key, value);
        } else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
