package com.wangxile.spring.framework.beans;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/23 0023 20:47
 * <p>
 * BeanWrapper 主要用于封装创建后的对象实例，代理对象（ Proxy object ）或者原生对象
 * ( Original Object ）都由 BeanWrapper 来保存。
 * <p>
 * https://blog.csdn.net/weixin_34152820/article/details/93192011
 * 辅助类，可以通过这个类快速修改对象的属性
 */
public class WQBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public WQBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    /**
     * 返回代理以后的class
     * 可能会是一个proxy
     *
     * @return
     */
    public Class<?> getWrappedClass() {
        return wrappedClass;
    }
}
