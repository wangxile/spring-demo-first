package com.wangxile.spring.framework.webmvc.servlet;

import com.wangxile.spring.framework.annotation.WQController;
import com.wangxile.spring.framework.annotation.WQRequestMapping;
import com.wangxile.spring.framework.context.WQApplicationContext;
import com.wangxile.spring.framework.webmvc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/18 0018 20:31
 */
public class WQDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<WQHandlerMapping> handlerMappingList = new ArrayList<WQHandlerMapping>();

    private Map<WQHandlerMapping, WQHandlerAdapter> handlerAdapterMap = new HashMap<WQHandlerMapping, WQHandlerAdapter>();

    private List<WQViewResolver> viewResolverList = new ArrayList<WQViewResolver>();

    private WQApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        context = new WQApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    public void initStrategies(WQApplicationContext context) {
        //有九种策略
        //针对每个请求，都会经过一些策略处理，然后生成最终结果
        //===============   九大组件 ===============//

        //文件上传解析，如果请求类型是multipart， 将通过MultipartResovler进行解析
        initMultipartResovler(context);

        //本地化解析
        initlocaleResolver(context);

        //主题解析
        initThemeResolver(context);

        //初始化HandlerMapping
        initHandlerMappings(context);

        //通过 HandlerAdapter 进行多类型的参数动态匹配
        initHandlerAdapters(context);

        //执行中遇到异常交给HandlerExceptionResolver来处理
        initHandlerExceptionResolvers(context);

        //直接将请求解析为视图名
        initRequestToViewNameTranslator(context);

        //通过 ViewResolver 实现动态模板的解析
        initViewResolvers(context);

        //Flash 映射管理器
        initFlashMapManager(context);
    }

    public void initFlashMapManager(WQApplicationContext context) {
    }

    public void initRequestToViewNameTranslator(WQApplicationContext context) {
    }

    public void initHandlerExceptionResolvers(WQApplicationContext context) {
    }

    public void initThemeResolver(WQApplicationContext context) {
    }

    public void initMultipartResovler(WQApplicationContext context) {
    }

    public void initlocaleResolver(WQApplicationContext context) {
    }

    /**
     * 将控制器中的requestMapping和method进行一一对应
     *
     * @param context
     */
    private void initHandlerMappings(WQApplicationContext context) {
        //首先从容器中获取所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if (!clazz.isAnnotationPresent(WQController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(WQRequestMapping.class)) {
                WQRequestMapping requestMapping = clazz.getAnnotation(WQRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(WQRequestMapping.class)) {
                    continue;
                }
                WQRequestMapping requestMapping = method.getAnnotation(WQRequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value()
                        .replaceAll("\\*", ".*"))
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappingList.add(new WQHandlerMapping(controller, method, pattern));
            }
        }
    }

    public void initHandlerAdapters(WQApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置Index，逐个从数组中取值，这样就和参数的顺序无关了
        for (WQHandlerMapping handlerMapping : this.handlerMappingList) {
            this.handlerAdapterMap.put(handlerMapping, new WQHandlerAdapter());
        }
    }

    public void initViewResolvers(WQApplicationContext context) {
        //在页面中输入http://localhost/first.html
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolverList.add(new WQViewResolver(templateRoot));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据请求的URL后的对应的handler
        WQHandlerMapping handlerMapping = getHandler(req);
        if (Objects.isNull(handlerMapping)) {
            processDispatchResult(req, resp, new WQModelAndView("404"));
            return;
        }
        WQHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        WQModelAndView modelAndView = handlerAdapter.handle(req, resp, handlerMapping);
        processDispatchResult(req, resp, modelAndView);
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                       WQModelAndView mv) throws Exception {
        //调用 viewResolver的resolveViewName()方法
        if (Objects.isNull(mv)) {
            return;
        }

        if (this.viewResolverList.isEmpty()) {
            return;
        }
        for (WQViewResolver viewResolver : this.viewResolverList) {
            WQView view = viewResolver.resolveViewName(mv.getViewName(), null);
            if (Objects.nonNull(view)) {
                view.render(mv.getModel(), request, response);
                return;
            }
        }
    }

    /**
     * 根据handlerMapping,获取对应的适配器
     *
     * @param handlerMapping
     * @return
     */
    public WQHandlerAdapter getHandlerAdapter(WQHandlerMapping handlerMapping) {
        if (this.handlerAdapterMap.isEmpty()) {
            return null;
        }
        WQHandlerAdapter handlerAdapter = this.handlerAdapterMap.get(handlerMapping);
        if (handlerAdapter.isSupports(handlerMapping)) {
            return handlerAdapter;
        }
        return null;
    }

    /**
     * 根据请求路径获取对应封装好的handlerMapping
     *
     * @param request
     * @return
     */
    public WQHandlerMapping getHandler(HttpServletRequest request) {
        if (this.handlerMappingList.isEmpty()) {
            return null;
        }

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (WQHandlerMapping handlerMapping : this.handlerMappingList) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }
}
