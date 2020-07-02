package com.wangxile.spring.framework.beans.config;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/23 0023 20:44
 * <p>
 * BeanDefinition 主要用于保存 Bean 相关的配置信息。
 */
public class WQBeanDefinition {

    /**
     * bean的全类名
     */
    private String beanClassName;

    /**
     * 懒加载
     */
    private Boolean lazyInit = false;

    /**
     * 保存 beanName,在 IoC 容器中存储的 key
     */
    private String beanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public Boolean getLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(Boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
