package com.obee.base.service;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/30 10:03
 */
public interface ChildDataHook<E> {
    // --- 新增事件 ---
    default void beforeAdd(E entity) {}
    default void afterAdd(E entity) {}

    // --- 修改事件 ---
    default void beforeUpdate(E entity) {}
    default void afterUpdate(E entity) {}

    // --- 删除事件 ---
    default void beforeDelete(Long id) {}
    default void afterDelete(Long id) {}
}
