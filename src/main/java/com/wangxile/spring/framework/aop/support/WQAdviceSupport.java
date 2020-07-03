package com.wangxile.spring.framework.aop.support;

import com.wangxile.spring.framework.aop.WQAopConfig;
import com.wangxile.spring.framework.aop.aspect.WQAfterReturningAdvice;
import com.wangxile.spring.framework.aop.aspect.WQAfterThrowingAdvice;
import com.wangxile.spring.framework.aop.aspect.WQMethodBeforeAdvice;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/2 0002 15:40
 * <p>
 * AdvisedSupport 主要完成对AOP配置的分析,生成拦截器链。
 * 其中 pointCutMatch()方法用来判断目标类是否符合切面规则，从而决定是否需要生成代理类，对目标方法进行增强
 * getInterceptorsAndDynamicInterceptionAdvice()方法主要根据AOP配置，
 * 将需要回调的方法封装成一个拦截器链并返回提供给外部获取
 */
public class WQAdviceSupport {

    private Class targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    private WQAopConfig aopConfig;

    public WQAdviceSupport(WQAopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass)
            throws Exception {
        List<Object> cached = methodCache.get(method);

        //缓存未命中，则进行下一步处理
        if (Objects.isNull(cached)) {
            Method methodLoad = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(methodLoad);
            this.methodCache.put(method, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse() throws IllegalAccessException, InstantiationException {
        String pointCut = aopConfig.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        String pointCutForClass = pointCut.substring(0,
                pointCut.lastIndexOf("\\(") - 4);
        //类适配
        pointCutClassPattern = Pattern.compile("class" + pointCutForClass
                .substring(pointCutForClass.lastIndexOf(" ") + 1));
        //方法适配
        Pattern pointCutMethodPattern = Pattern.compile(pointCut);

        methodCache = new HashMap<Method, List<Object>>();
        try {
            //获取切面类(就是用@Asjpect修饰的类)，并将类中的所有方法存在map中
            Class aspectClass = Class.forName(aopConfig.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            //在这里得到的方法都是原生方法
            for (Method method : targetClass.getMethods()) {
                String methodStr = method.toString();
                if (methodStr.contains("throws")) {
                    methodStr = methodStr.substring(0, methodStr.lastIndexOf("throws")).trim();
                    Matcher matcher = pointCutMethodPattern.matcher(methodStr);
                    if (matcher.matches()) {
                        //能满足切面规则的类，添加到aop配置中
                        List<Object> adviceList = new LinkedList<Object>();

                        //前置通知
                        if (StringUtils.isNotBlank(aopConfig.getAspectBefore())) {
                            adviceList.add(new WQMethodBeforeAdvice(aspectMethods.get(aopConfig.getAspectBefore()),
                                    aspectClass.newInstance()));
                        }

                        //后置通知
                        if (StringUtils.isNotBlank(aopConfig.getAspectAfter())) {
                            adviceList.add(new WQAfterReturningAdvice(aspectMethods.get(aopConfig.getAspectBefore()),
                                    aspectClass.newInstance()));
                        }

                        //异常通知
                        if (StringUtils.isNotBlank(aopConfig.getAspectAfterThrow())) {
                            WQAfterThrowingAdvice wqAfterThrowingAdvice
                                    = new WQAfterThrowingAdvice(aspectMethods.get(aopConfig.getAspectBefore()),
                                    aspectClass.newInstance());
                            wqAfterThrowingAdvice.setThrowingName(aopConfig.getAspectAfterThrowingName());
                            adviceList.add(wqAfterThrowingAdvice);
                        }
                        methodCache.put(method, adviceList);

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) throws InstantiationException, IllegalAccessException {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
