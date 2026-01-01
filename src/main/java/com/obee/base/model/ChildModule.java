package com.obee.base.model;

import com.baomidou.mybatisplus.extension.service.IService;
import com.obee.base.service.BaseSingleService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:03
 *
 * 子模块配置注解 (仅用于 @ModuleInfo 内部)
 */
@Target({}) // 不单独使用，只作为属性
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildModule {
    /**
     * 前端 JSON 中的 Key (例如 "items", "pays")
     */
    String key();

    /**
     * 子表实体类 Class
     */
    Class<? extends BaseEntity> entity();

    /**
     * 子表对应的 Service Class
     * 基类会自动从 Spring 容器中获取这个类型的 Bean
     */
    Class<? extends BaseSingleService> service();

    /**
     * 子表中关联主表的外键字段名 (Java 属性名，非数据库列名)
     * 例如 "orderId"
     */
    String fkField();
}
