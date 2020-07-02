package com.wangxile.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/11 0011 19:47
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WQService {
    String value() default " ";
}
