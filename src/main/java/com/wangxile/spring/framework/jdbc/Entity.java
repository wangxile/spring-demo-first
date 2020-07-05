package com.wangxile.spring.framework.jdbc;

import java.lang.annotation.*;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2020/7/4
 * @Modified by:
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Entity {
    String name() default "";
}