package com.wangxile.spring.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/30 0030 9:12
 * <p>
 * HandlerMapping 主要用来保存 URL 和 Method 的对应关系，
 */
public class WQHandlerMapping {

    /**
     * 目标方法所在的controller对象
     */
    private Object controller;

    /**
     * 对应的目标方法
     */
    private Method method;

    /**
     * Url的封装
     */
    private Pattern pattern;

    public WQHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
