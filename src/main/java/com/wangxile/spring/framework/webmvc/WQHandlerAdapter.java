package com.wangxile.spring.framework.webmvc;

import com.alibaba.fastjson.JSON;
import com.wangxile.spring.framework.annotation.WQRequestMapping;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/30 0030 9:15
 * <p>
 * 原生的spring的handleAdapter主要完成请求传递到服务端的参数列表与 Method 实参列表的对应关系，完成参数值的类型转换
 * 核心方法是handler();
 */
public class WQHandlerAdapter {

    public boolean isSupports(Object handler) {
        return (handler instanceof WQHandlerMapping);
    }

    public WQModelAndView handle(HttpServletRequest req, HttpServletResponse resp,
                                 Object handler) throws InvocationTargetException, IllegalAccessException {
        WQHandlerMapping handlerMapping = (WQHandlerMapping) handler;

        //每个方法有参数列表，这里保存的是形参列表
        Map<String, Integer> methodParamMap = new HashMap<String, Integer>();

        Annotation[][] annotationArr = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < annotationArr.length; i++) {
            for (Annotation annotation : annotationArr[i]) {
                if (annotation instanceof WQRequestMapping) {
                    String paramName = ((WQRequestMapping) annotation).value();
                    if (StringUtils.isNotBlank(paramName)) {
                        methodParamMap.put(paramName, i);
                    }
                }
            }
        }

        // 获取方法参数列表  主要处理request 和 response 参数
        Class<?>[] paramClassTypes = handlerMapping.getMethod().getParameterTypes();
        // 提取方法中的 request 和 response 参数
        for (int i = 0; i < paramClassTypes.length; i++) {
            Class paramClassType = paramClassTypes[i];
            if (paramClassType == HttpServletRequest.class || paramClassType == HttpServletResponse.class) {
                methodParamMap.put(paramClassType.getName(), i);
            }
        }

        // 保存请求的url参数列表
        Map<String, String[]> requestParamMap = req.getParameterMap();
        // 构造实参列表
        Object[] paramValues = new Object[paramClassTypes.length];
        for (Map.Entry<String, String[]> requestParam : requestParamMap.entrySet()) {
            String value = Arrays.toString(requestParam.getValue())
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if (!methodParamMap.containsKey(requestParam.getKey())) {
                continue;
            }
            //获取参数在方法中的下标
            int index = methodParamMap.get(requestParam.getKey());
            paramValues[index] = caseStringValue(paramClassTypes[index], value);

        }

        //单独处理req
        if (methodParamMap.containsKey(HttpServletRequest.class.getName())) {
            int index = methodParamMap.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        //单独处理resp
        if (methodParamMap.containsKey(HttpServletResponse.class.getName())) {
            int index = methodParamMap.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object returnValue = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (Objects.isNull(returnValue)) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == WQModelAndView.class;
        if (isModelAndView) {
            return (WQModelAndView) returnValue;
        }
        return null;
    }

    public Object caseStringValue(Class<?> clazz, String value) {
        if (clazz == String.class) {
            return value;
        }
        // TODO 使用这种方式，暂时测试没问题
        return JSON.parseObject(value, clazz);
    }
}
