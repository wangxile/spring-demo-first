package com.wangxile.spring.framework.aop.aspect;

import com.wangxile.spring.framework.aop.interceptor.WQMethodInterceptor;
import com.wangxile.spring.framework.aop.interceptor.WQMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 20:03
 */
public class WQAfterThrowingAdvice extends WQAbstractAspectJAdvice
        implements WQAdvice, WQMethodInterceptor {

    private String throwingName;

    private WQMethodInvocation methodInvocation;

    public WQAfterThrowingAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    public Object invoke(WQMethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(methodInvocation, null, ex.getCause());
            throw ex;
        }
    }
}
