package com.wangxile.spring.framework.aop.aspect;

import com.wangxile.spring.framework.aop.interceptor.WQMethodInterceptor;
import com.wangxile.spring.framework.aop.interceptor.WQMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 19:31
 * <p>
 * MethodBeforeAdvice 继承 AbstractAspectJAdvice 实现 Advice MethodInterceptor接口
 * invoke（）中控制前置通知的调用顺序。
 */
public class WQMethodBeforeAdvice extends WQAbstractAspectJAdvice
        implements WQAdvice, WQMethodInterceptor {

    private WQJoinPoint joinPoint;

    /**
     * 装配切面逻辑方法
     *
     * @param aspectMethod
     * @param target
     */
    public WQMethodBeforeAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(WQMethodInvocation methodInvocation) throws Throwable {
        this.joinPoint = methodInvocation;
        this.before(methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getThis());
        return methodInvocation.proceed();
    }

}
