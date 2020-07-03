package com.wangxile.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 15:19
 * <p>
 * 定义一个切点的抽象，这是 AOP 的基础组成单元
 * 我们可以理解为这是某一个业务方法的附加信息
 * 可想而知，切点应该包含业务方法本身、实参列表和方法所属的实例对象，
 * 还可以在JoinPoint 中添加自定义属性
 */
public interface WQJoinPoint {

    /**
     * 获取业务方法本身
     *
     * @return
     */
    Method getMethod();

    /**
     * 获取参数
     *
     * @return
     */
    Object[] getArguments();

    /**
     * 获取该方法所属的实例对象
     *
     * @return
     */
    Object getThis();

    /**
     * JoinPoint中添加自定义属性
     *
     * @param key
     * @param value
     */
    void setUserAttribute(String key, Object value);

    /**
     * 获取自定义属性
     *
     * @param key
     * @return
     */
    Object getUserAttribute(String key);

}
