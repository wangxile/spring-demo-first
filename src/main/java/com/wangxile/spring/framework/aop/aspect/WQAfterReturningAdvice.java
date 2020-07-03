package com.wangxile.spring.framework.aop.aspect;

import com.wangxile.spring.framework.aop.interceptor.WQMethodInterceptor;
import com.wangxile.spring.framework.aop.interceptor.WQMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 20:00
 */
public class WQAfterReturningAdvice extends WQAbstractAspectJAdvice
        implements WQAdvice, WQMethodInterceptor {

    private WQJoinPoint joinPoint;

    public WQAfterReturningAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(WQMethodInvocation methodInvocation) throws Throwable {
        Object returnValue = methodInvocation.proceed();
        this.joinPoint = methodInvocation;
        this.afterReturning(returnValue, methodInvocation.getMethod(),
                methodInvocation.getArguments(), methodInvocation.getThis());
        return returnValue;
    }

}
