package com.obee.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.obee.base.model.BaseEntity;

import java.util.List;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:03
 */
public interface BaseSingleService<T extends BaseEntity> {

    /**
     * 新增 (带钩子)
     */
    boolean create(T entity);

    /**
     * 修改 (带钩子)
     */
    boolean modify(T entity);

    /**
     * 删除 (带钩子)
     */
    boolean remove(Long id);

    /**
     * 根据ID查询
     */
    T get(Long id);

    /**
     * 分页查询
     */
    IPage<T> page(long current, long size, T condition);

    /**
     * 列表查询
     */
    List<T> list(T condition);
}
