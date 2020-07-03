package com.wangxile.spring.framework.aop.interceptor;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 15:25
 * <p>
 * 方法拦截器是 AOP 代码增强的基本组成单元
 * 其子类主要有 MethodBeforeAdvice AfterReturningAdvice AfterThrowingExceptionAdvice
 * <p>
 * 在Spring Aop框架中，MethodInterceptor接口被用来拦截指定的方法，对方法进行增强。
 */
public interface WQMethodInterceptor {
    /**
     * 方法拦截器顶层接口
     *
     * @param mi
     * @return
     * @throws Throwable
     */
    Object invoke(WQMethodInvocation mi) throws Throwable;
}
