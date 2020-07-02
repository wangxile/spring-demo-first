package com.wangxile.spring.framework.annotation;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/11 0011 19:50
 */

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WQRequestMapping {
    String value() default "";
}
