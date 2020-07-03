package com.wangxile.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 19:16
 * 使用模板模式设计 WQAbstractAspectJAdvice 类，封装拦截器回调的通用逻辑，主要封装反射
 * 动态调用方法，其子类只需要控制调用顺序即可。
 */
public class WQAbstractAspectJAdvice implements WQAdvice {

    private Method aspectMethod;

    private Object aspectTarget;

    public WQAbstractAspectJAdvice(Method method, Object aspectTarget) {
        this.aspectMethod = method;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(WQJoinPoint joinPoint, Object returnValue, Throwable ex)
            throws Throwable {
        Class<?>[] paramsTypes = this.aspectMethod.getParameterTypes();
        if (null == paramsTypes || paramsTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramsTypes.length];
            for (int i = 0; i < paramsTypes.length; i++) {
                if (paramsTypes[i] == WQJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramsTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramsTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }

}
