package com.wangxile.spring.framework.annotation;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/11 0011 19:51
 */

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WQRequestParam {
    String value() default "";
}
