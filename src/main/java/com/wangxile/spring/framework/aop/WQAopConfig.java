package com.wangxile.spring.framework.aop;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 15:33
 * <p>
 * 定义 AOP 的配置信息的封装对象，以方便在之后的代码中相互传递。
 */
public class WQAopConfig {

    /**
     * 切面表达式
     */
    private String pointCut;

    /**
     * 要织入的切面类
     */
    private String aspectClass;

    /**
     * 前置通知方法名
     */
    private String aspectBefore;

    /**
     * 后置通知方法名
     */
    private String aspectAfter;

    /**
     * 异常通知方法名
     */
    private String aspectAfterThrow;

    /**
     * 需要通知的异常类型
     */
    private String aspectAfterThrowingName;

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }

    public String getAspectClass() {
        return aspectClass;
    }

    public void setAspectClass(String aspectClass) {
        this.aspectClass = aspectClass;
    }

    public String getAspectBefore() {
        return aspectBefore;
    }

    public void setAspectBefore(String aspectBefore) {
        this.aspectBefore = aspectBefore;
    }

    public String getAspectAfter() {
        return aspectAfter;
    }

    public void setAspectAfter(String aspectAfter) {
        this.aspectAfter = aspectAfter;
    }

    public String getAspectAfterThrow() {
        return aspectAfterThrow;
    }

    public void setAspectAfterThrow(String aspectAfterThrow) {
        this.aspectAfterThrow = aspectAfterThrow;
    }

    public String getAspectAfterThrowingName() {
        return aspectAfterThrowingName;
    }

    public void setAspectAfterThrowingName(String aspectAfterThrowingName) {
        this.aspectAfterThrowingName = aspectAfterThrowingName;
    }
}
