package com.wangxile.spring.framework.webmvc;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Locale;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/1 0001 17:43
 * <p>
 * 原生 Spring viewResolver 主要完成模板名称和模板解析引擎的匹配。
 * 通过在 Serlvet 中调勇resolveViewName()方法来获得对应的view
 */
public class WQViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    private String viewName;

    public WQViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public WQView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (StringUtils.isBlank(viewName)) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName)
                .replaceAll("/+", "/"));
        return new WQView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
