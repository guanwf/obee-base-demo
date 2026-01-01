package com.obee.base.model;

import java.lang.annotation.*;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModuleInfo {
    String code();
    String name() default "";

    // 新增：子表配置数组
    ChildModule[] children() default {};
}
