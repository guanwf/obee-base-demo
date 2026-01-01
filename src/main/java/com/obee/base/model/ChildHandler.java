package com.obee.base.model;


import com.baomidou.mybatisplus.extension.service.IService;
import com.obee.base.service.BaseSingleService;

/**
 * 内部使用的子表处理器
 */
public record ChildHandler (
    Class<? extends BaseEntity> entityClass,
    BaseSingleService service,
    String fkFieldName // 存储外键字段名 (如 "orderId")
){}
