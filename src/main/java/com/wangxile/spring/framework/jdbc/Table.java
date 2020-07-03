package com.wangxile.spring.framework.jdbc;

import java.lang.annotation.*;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 10:56
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String name() default "";
}
