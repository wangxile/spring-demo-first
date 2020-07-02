package com.wangxile.spring.framework.version.v3;

import com.alibaba.fastjson.JSON;
import com.wangxile.spring.framework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author : R&M www.rmworking.com/blog
 * 2019/10/3 16:10
 * jsoup_demo
 * org.qnloft.mvcframework.v3.servlet
 */
public class WQDispatcherServlet extends HttpServlet {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger("QnDispatcherServlet");
    /**
     * 保存配置文件中的内容
     */
    private Properties contextConfigProperties = new Properties();

    /**
     * 保存扫描的所有类名
     */
    private List<String> classNames = new ArrayList<>();

    /**
     * ioc容器
     */
    private Map<String, Object> ioc = new HashMap<>();

    /**
     * 保存url和Method的对应关系
     */
    private List<Handler> handlerMapping = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Handler handler = getHandler(req);

        // 如果访问的url不在handlerMapping中，则返回404
        if (handler == null) {
            // 解决中文乱码问题
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("页面跑丢了！！！");
            return;
        }
        // 保存请求的url参数列表
        Map<String, String[]> params = req.getParameterMap();
        // 获取方法参数列表
        Class<?>[] paramTypes = handler.getParameterTypes();
        // 保存赋值参数的位置
        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if (handler.paramIndexMapping.containsKey(param.getKey())) {
                //获取参数在方法中的下标
                int index = handler.paramIndexMapping.get(param.getKey());
                paramValues[index] = convert(paramTypes[index], value);
            }
        }

        //单独处理req
        if (handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        //单独处理resp
        if (handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object returnValue = handler.method.invoke(handler.controller, paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        resp.getWriter().write(returnValue.toString());

    }

    private Object convert(Class<?> type, String v) {
        if (type == String.class) {
            return v;
        }
        // TODO 使用这种方式，暂时测试没问题
        return JSON.parseObject(v, type);
    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }
        // 获取访问的uri
        String url = req.getRequestURI();
        logger.info("用户访问的url是：" + url);
        String contextPath = req.getContextPath();
        logger.info("contextPath == " + contextPath);
        for (Handler handler : handlerMapping) {
            // url匹配
            Matcher matcher = handler.pattern.matcher(url);
            // 匹配成功
            if (matcher.matches()) {
                return handler;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // spring 中只要是`do`开头的方法，都是干活的小弟
        // 1. 加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        // 2. 扫描所有相关的类
        doScanner(contextConfigProperties.getProperty("scanPackage"));
        // 3. 初始化所有相关联的类，并且将所有的扫描到的类实例化放入到ioc容器中
        doInstance();
        // 4. 自动化依赖注入
        doAutowired();
        // 5. 初始化handlerMapping
        initHandlerMapping();
    }

    private void doLoadConfig(String contextConfigLocation) {
        // 拿到spring配置文件的路径，读取文件中所有内容
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation)) {
            contextConfigProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        // 拿到包名,实际上就是把`.`替换成`/`
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        if (url != null) {
            File classDir = new File(url.getFile());
            if (classDir.listFiles() != null) {
                for (File file : classDir.listFiles()) {
                    // 如果是一个文件夹，说明是一个子包
                    // 需要递归读取到子包下面的所有class
                    if (file.isDirectory()) {
                        doScanner(scanPackage + "." + file.getName());
                    } else {
                        if (!file.getName().endsWith(".class")) {
                            continue;
                        }
                        String className = scanPackage + "." + file.getName().replace(".class", "");
                        classNames.add(className);
                    }
                }
            }
        }
    }

    private void doInstance() {
        // 判断有没有扫描到类
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            // 拿到class对象，就可以反射对象
            try {
                Class<?> clazz = Class.forName(className);
                // 通过反射机制，实例化对象
                // 判断对象是否是controller和service
                if (clazz.isAnnotationPresent(WQController.class)) {
                    Object instance = clazz.newInstance();
                    // 将类名首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, instance);
                } else if (clazz
                        .isAnnotationPresent(WQService.class)) {
                    // 1.如果自己指定了beanName名称，优先采用自定义名称
                    WQService service = clazz.getAnnotation(WQService.class);
                    String beanName = service.value();
                    // 2.类名首字母小写
                    if (Objects.equals("", beanName.trim())) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                    // 3.根据类型自动赋值
                    for (Class<?> aClass : clazz.getInterfaces()) {
                        if (ioc.containsKey(aClass.getName())) {
                            throw new Exception("这个 [" + aClass.getName() + "] 已经存在！！");
                        }
                        ioc.put(aClass.getName(), instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("spring IOC list :" + JSON.toJSONString(ioc));
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取所有字段，包括private、protected、default类型
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                // 判断是否有`QnAutowired`注解
                if (field.isAnnotationPresent(WQAutowired.class)) {
                    logger.info("field 原来的样子：" + JSON.toJSONString(field));
                    WQAutowired autowired = field.getAnnotation(WQAutowired.class);
                    // 如果用户没有自定义`beanName`,默认就根据类型注入
                    String beanName = autowired.value().trim();
                    if (Objects.equals(null, beanName) || Objects.equals("", beanName)) {
                        beanName = field.getType().getName();
                    }

                    // 如果是public以外的类型，只要加了`@QnAutowired`注解都要强制赋值
                    field.setAccessible(true);

                    // 用反射机制动态给字段赋值
                    try {
                        logger.info("entry.getValue() ：" + JSON.toJSONString(field));
                        logger.info(beanName + " >>> search ioc ：" + JSON.toJSONString(ioc.get(beanName)));
                        if (ioc.get(beanName) == null && Objects.equals("", ioc.get(beanName))) {
                            throw new Exception(beanName + " 不存在，或无法初始化！");
                        }
                        field.set(entry.getValue(), ioc.get(beanName));
                        logger.info("field 赋值后的样子：" + JSON.toJSONString(field));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            logger.info("clazz == >> " + JSON.toJSONString(clazz));
            if (!clazz.isAnnotationPresent(WQController.class)) {
                continue;
            }
            // 保存写在类上面的`@QnRequestMapping("/demo")`
            String baseUrl = "";
            if (clazz.isAnnotationPresent(WQRequestMapping.class)) {
                WQRequestMapping requestMapping = clazz.getAnnotation(WQRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            // 获取`Method`的url配置
            Method[] methods = clazz.getMethods();
            // 默认获取所有的`public`类型的方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(WQRequestMapping.class)) {
                    return;
                }
                WQRequestMapping requestMapping = method.getAnnotation(WQRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(url);
                // 初始化`Handle`参数  TODO 策略模式
                handlerMapping.add(new Handler(entry.getValue(), method, pattern));
                logger.info("mapped :[ " + url + "] ," + method);
            }
        }
        logger.info("handlerMapping list: " + JSON.toJSONString(handlerMapping));
    }

    private String toLowerFirstCase(String s) {
        if (s != null && !Objects.equals("", s)) {

            char[] chars = s.toCharArray();
            // 大写字母的ASCII码和小写字母相差32，所以将第一个字符加32就变成小写了
            chars[0] += 32;
            return String.valueOf(chars);
        }
        return null;
    }


    private class Handler {
        /**
         * 保存方法对应的实例
         */
        protected Object controller;
        /**
         * 保存映射方法
         */
        protected Method method;
        protected Pattern pattern;
        /**
         * 参数顺序
         */
        protected Map<String, Integer> paramIndexMapping;

        /**
         * 构造一个Handle的基本参数
         *
         * @param controller
         * @param method
         * @param pattern
         */
        protected Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }

        /**
         * @param method
         */
        private void putParamIndexMapping(Method method) {
            // 提取方法中加了注解的参数 主要处理带注解的GPRequestParam
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation annotation : pa[i]) {
                    if (annotation instanceof WQRequestParam) {
                        // 参数名称
                        String paramName = ((WQRequestParam) annotation).value();
                        logger.info("paramName == " + paramName);
                        if (!Objects.equals("", paramName.trim())) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }

            // 获取方法参数列表  主要处理request 和 response 参数
            Class<?>[] paramTypes = method.getParameterTypes();
            // 提取方法中的 request 和 response 参数
            for (int i = 0; i < paramTypes.length; i++) {
                Class paramType = paramTypes[i];
                if (paramType == HttpServletRequest.class) {
                    paramIndexMapping.put(paramType.getName(), i);
                } else if (paramType == HttpServletResponse.class) {
                    paramIndexMapping.put(paramType.getName(), i);
                }
            }
        }

        /**
         * @return
         */
        protected Class<?>[] getParameterTypes() {
            return method.getParameterTypes();
        }
    }
}
