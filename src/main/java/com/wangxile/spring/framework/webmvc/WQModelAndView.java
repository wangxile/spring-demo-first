package com.wangxile.spring.framework.webmvc;

import java.util.Map;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/1 0001 17:39
 * <p>
 * 原生 spring中 ModelAndView 类主要用于封装页面模板和要往页面传送的参数的对应关系。
 */
public class WQModelAndView {

    /**
     * 页面模板名称
     */
    private String viewName;

    /**
     * 往页面传送的参数
     */
    private Map<String, ?> model;

    public WQModelAndView(String viewName) {
        this(viewName, null);
    }

    private WQModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
