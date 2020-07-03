package com.wangxile.spring.framework.context;

import com.wangxile.spring.framework.annotation.WQAutowired;
import com.wangxile.spring.framework.annotation.WQController;
import com.wangxile.spring.framework.annotation.WQService;
import com.wangxile.spring.framework.aop.WQAopConfig;
import com.wangxile.spring.framework.aop.WQAopProxy;
import com.wangxile.spring.framework.aop.WQCglibAopProxy;
import com.wangxile.spring.framework.aop.WQJdkDynamicAopProxy;
import com.wangxile.spring.framework.aop.support.WQAdviceSupport;
import com.wangxile.spring.framework.beans.WQBeanWrapper;
import com.wangxile.spring.framework.beans.config.WQBeanDefinition;
import com.wangxile.spring.framework.beans.config.WQBeanPostProcessor;
import com.wangxile.spring.framework.beans.support.WQBeanDefinitionReader;
import com.wangxile.spring.framework.context.support.WQDefaultlistableBeanFactory;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/28 0028 17:34
 * <p>
 * 分析IOC DI MVC AOP
 */
public class WQApplicationContext extends WQDefaultlistableBeanFactory {

    private String[] configLocations;

    private WQBeanDefinitionReader reader;

    /**
     * 单例的ioc容器缓存
     */
    private Map<String, Object> factoryBeanObjectMap = new ConcurrentHashMap<String, Object>();

    /**
     * 通用的IOC容器
     */
    private Map<String, WQBeanWrapper> factoryBeanWrapperMap = new ConcurrentHashMap<String, WQBeanWrapper>();

    public WQApplicationContext(String... locations) {
        this.configLocations = locations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.设置配置文件路径
        reader = new WQBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关类，把他们封装成BeanDefinition
        List<WQBeanDefinition> beanDefinitionList = reader.loadBeanDefinitions();

        //3.注册，将配置信息放到容器中(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitionList);

        //4.把不是延迟加载的类，提前初始化
        doAutoWrited();
    }

    private void doRegisterBeanDefinition(List<WQBeanDefinition> beanDefinitionList) {
        for (WQBeanDefinition beanDefinition : beanDefinitionList) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getBeanName())) {
                throw new IllegalArgumentException("存在重复的bean");
            }
            super.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
    }

    private void doAutoWrited() {
        for (Map.Entry<String, WQBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().getLazyInit()) {
                getBean(beanName);
            }
        }
    }


    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    /**
     * 依赖注入，从这里开始读取BeanDefinition中的信息
     * 然后通过反射建一个实例并返回
     * Spring做法是，不会最原始的对象放出去，会用BeanWrapper来进行包装
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        WQBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            WQBeanPostProcessor beanPostProcessor = new WQBeanPostProcessor();

            Object instance = instantiateBean(beanDefinition);
            if (Objects.isNull(instance)) {
                return null;
            }

            //初始化前调用
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            WQBeanWrapper beanWrapper = new WQBeanWrapper(instance);
            this.factoryBeanWrapperMap.put(beanName, beanWrapper);

            //初始化后调用
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

            populateBean(beanName, instance);

            return this.factoryBeanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析beanDefinition,返回实例bean
     *
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(WQBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        String beanName = beanDefinition.getBeanName();
        try {
            if (this.factoryBeanObjectMap.containsKey(beanName)) {
                instance = this.factoryBeanObjectMap.get(beanName);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //AOP生效，读取配置文件
                WQAdviceSupport adviceSupport = instantionAopConfig(beanDefinition);
                adviceSupport.setTarget(instance);
                adviceSupport.setTargetClass(clazz);
                if (adviceSupport.pointCutMatch()) {
                    //判断当前bean满足切面规则
                    instance = createProxy(adviceSupport).getProxy();
                }
                this.factoryBeanObjectMap.put(beanDefinition.getBeanName(), instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 依赖注入实现，遍历字段进行注入
     *
     * @param beanName
     * @param instance
     */
    public void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(WQController.class)
                || clazz.isAnnotationPresent(WQService.class))) {
            return;
        }
        Field[] fields = clazz.getFields();
        try {
            for (Field field : fields) {
                if (!field.isAnnotationPresent(WQAutowired.class)) {
                    continue;
                }
                WQAutowired autowired = field.getAnnotation(WQAutowired.class);
                String autowiredValue = autowired.value().trim();
                if (StringUtils.isBlank(autowiredValue)) {
                    autowiredValue = field.getType().getName();
                }
                field.setAccessible(true);
                field.set(instance, this.factoryBeanWrapperMap.get(autowiredValue).getWrappedInstance());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public String[] getBeanDefinitionNames() {
        return super.beanDefinitionMap.keySet().toArray(new String[super.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return super.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return reader.getConfig();
    }

    /**
     * 读取配置文件，初始化AOP配置
     *
     * @param beanDefinition
     * @return
     */
    private WQAdviceSupport instantionAopConfig(WQBeanDefinition beanDefinition) {
        WQAopConfig config = new WQAopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new WQAdviceSupport(config);
    }

    /**
     * 生成AOP代理对象
     *
     * @param adviceSupport
     * @return
     */
    private WQAopProxy createProxy(WQAdviceSupport adviceSupport) {
        Class targetClass = adviceSupport.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new WQJdkDynamicAopProxy(adviceSupport);
        }
        return new WQCglibAopProxy(adviceSupport);
    }
}
