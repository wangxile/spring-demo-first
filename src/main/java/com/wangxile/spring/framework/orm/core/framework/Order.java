package com.wangxile.spring.framework.orm.core.framework;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 14:55
 * 排序组件
 */
public class Order {

    /**
     * 升序还是降序
     */
    private boolean ascending;

    /**
     * 根据哪个字段升序，降序
     */
    private String propertyName;

    public Order(boolean ascending, String propertyName) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    public static Order asc(String propertyName) {
        return new Order(true, propertyName);
    }

    public static Order desc(String propertyName) {
        return new Order(false, propertyName);
    }

    @Override
    public String toString() {
        return propertyName + ' ' + (ascending ? "asc" : "desc");
    }

}
