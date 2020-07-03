package com.wangxile.spring.framework.aop.aspect;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 20:17
 * <p>
 * 定义个织入的切面逻辑 也就是要针对目标代理对象增强的逻辑
 * 本类主要完成对方法调用的监控，监听目标方法每次执行所消耗的时间
 */
public class LogAspect {
    public void before(WQJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),
                System.currentTimeMillis());
    }

    public void after(WQJoinPoint joinPoint) {

    }

    public void afterThrowing(WQJoinPoint joinPoint) {

    }
}
