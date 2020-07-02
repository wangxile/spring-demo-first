package com.wangxile.spring.framework.version.v2;

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

/**
 * @author : R&M www.rmworking.com/blog
 * 2019/10/2 10:24
 * jsoup_demo
 * org.qnloft.mvcframework.v1.servlet
 */
public class WQDispatcherServlet extends HttpServlet {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger("GPDispatcherServlet");

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
    private Map<String, Method> handlerMapping = new HashMap<>();

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
        // 获取访问的uri
        String url = req.getRequestURI();

        // 如果访问的url不在handlerMapping中，则返回404
        if (!this.handlerMapping.containsKey(url)) {
            // 解决中文乱码问题
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("页面跑丢了！！！");
            return;
        }

        Method method = this.handlerMapping.get(url);
        // url请求参数列表
        Map<String, String[]> urlParams = req.getParameterMap();

        // 获取方法参数列表
        Class<?>[] methodParameterTypes = method.getParameterTypes();

        // 保存赋值参数的位置
        Object[] paramValues = new Object[methodParameterTypes.length];
        for (int i = 0; i < methodParameterTypes.length; i++) {
            Class paramType = methodParameterTypes[i];
            logger.info("paramType == " + JSON.toJSONString(paramType));
            if (paramType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else if (paramType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else {
                // 提取方法中加了注解的参数
                //https://blog.csdn.net/u011710466/article/details/52888387
                Annotation[][] pa = method.getParameterAnnotations();
                for (Annotation annotation : pa[i]) {
                    if (annotation instanceof WQRequestParam) {
                        // 参数名称
                        String paramName = ((WQRequestParam) annotation).value();
                        logger.info("paramName == " + paramName);
                        if (!Objects.equals("", paramName.trim())) {
                            //从URL请求的参数中，获取到对应的参数值
                            String value = Arrays.toString(urlParams.get(paramName)).replaceAll("\\[|\\]", "")
                                    .replaceAll("\\s", ",");

                            // TODO 这里不应该判断全部类型，是否有更好的解决方案，可以去spring源码中找找
                            if (paramType == String.class) {
                                paramValues[i] = value;
                            } else if (paramType == Integer.class) {
                                paramValues[i] = Integer.valueOf(value);
                            }
                            // 剩下的类型 Double 等等 就在不赘述了....
                        }
                    }
                }
            }
        }

        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        // 给指定的方法，动态的传人参数
        // params.get("name")只是实现了 `/demo/query?name=qnloft` 这个请求，但是如果是`/demo/add?a=1&b=2`这个请求就没有实现了
//        method.invoke(ioc.get(beanName), req, resp, params.get("name")[0]);
        method.invoke(ioc.get(beanName), paramValues);
        logger.info("method :" + JSON.toJSONString(method));
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
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
                } else if (clazz.isAnnotationPresent(WQService.class)) {
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
            if (clazz.isAnnotationPresent(WQController.class)) {
                // 保存写在类上面的`@QnRequestMapping("/demo")`
                String baseUrl = "";
                if (clazz.isAnnotationPresent(WQRequestMapping.class)) {
                    WQRequestMapping requestMapping = clazz.getAnnotation(WQRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                // 默认获取所有的`public`类型的方法
                for (Method method : clazz.getMethods()) {
                    if (method.isAnnotationPresent(WQRequestMapping.class)) {
                        WQRequestMapping requestMapping = method.getAnnotation(WQRequestMapping.class);
                        String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                        handlerMapping.put(url, method);
                        logger.info("mapped :[ " + url + "] ," + method);
                    }
                }
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
}
