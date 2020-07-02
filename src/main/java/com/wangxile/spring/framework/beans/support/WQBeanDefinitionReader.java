package com.wangxile.spring.framework.beans.support;

import com.wangxile.spring.framework.beans.config.WQBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/28 0028 17:37
 * <p>
 * 主要完成对配置文件 application.properties的解析工作
 */
public class WQBeanDefinitionReader {

    /**
     * 固定文件中配置的key
     */
    private final String SCAN_PACKAGE = "scanPackage";

    private List<String> registryBeanClassList = new ArrayList<String>();

    private Properties config = new Properties();

    public WQBeanDefinitionReader(String... locations) {
        //根据路径获取配置文件流
        InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //扫描指定路径
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public void doScanner(String scanPackage) {
        //转换为文件路径,实际上就是把.变成/
        URL url = this.getClass().getClassLoader()
                .getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String beanClass = scanPackage + "." + file.getName().replace(".class", "");
                registryBeanClassList.add(beanClass);
            }
        }
    }

    public List<WQBeanDefinition> loadBeanDefinitions() {
        List<WQBeanDefinition> result = new ArrayList<WQBeanDefinition>();
        try {
            for (String registryBeanClass : registryBeanClassList) {
                Class<?> beanClass = Class.forName(registryBeanClass);
                if (beanClass.isInterface()) {
                    //过滤接口，不进行扫描
                    continue;
                }

                //将类解析成beanDefinition
                //getName —- com.se7en.test.Main
                //getSimpleName —- Main
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),
                        beanClass.getName()));

                //将类的接口也解析成beanDefinition
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(toLowerFirstCase(i.getName()),
                            beanClass.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;

    }


    /**
     * 把每一个配置信息解析成 BeanDefinition
     *
     * @param beanName      bean简称
     * @param beanClassName bean全类名
     * @return
     */
    private WQBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        WQBeanDefinition gpBeanDefinition = new WQBeanDefinition();
        gpBeanDefinition.setBeanName(beanName);
        gpBeanDefinition.setBeanClassName(beanClassName);
        return gpBeanDefinition;
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

    public List<String> getRegistryBeanClassList() {
        return registryBeanClassList;
    }

    public void setRegistryBeanClassList(List<String> registryBeanClassList) {
        this.registryBeanClassList = registryBeanClassList;
    }

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }
}
