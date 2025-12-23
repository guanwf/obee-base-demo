package com.obee.base.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obee.base.model.BaseEntity;
import com.obee.base.service.BaseSingleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:05
 */
@Slf4j
public abstract class BaseSingleServiceImpl<T extends BaseEntity>
//        extends ServiceImpl<M, T>
        implements BaseSingleService<T> {

    // ================== 扩展钩子 (Hooks) ==================
    // 采用 protected 允许子类覆盖，但对外不可见

    protected void beforeCreate(T entity) {
    }

    protected void afterCreate(T entity) {
    }

    protected void beforeModify(T entity) {
    }

    protected void afterModify(T entity) {
    }

    protected void beforeRemove(Long id) {
    }

    protected void afterRemove(Long id) {
    }

    // ================== 接口实现 ==================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(T entity) {
        beforeCreate(entity);
        // super.save() 是 ServiceImpl 提供的方法
        boolean result = true;//super.save(entity);
        if (result) {
            afterCreate(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(T entity) {
        beforeModify(entity);
        // super.updateById() 是 ServiceImpl 提供的方法
        boolean result = true;//super.updateById(entity);
        if (result) {
            afterModify(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long id) {
        beforeRemove(id);
        boolean result = true;//super.removeById(id);
        if (result) {
            afterRemove(id);
        }
        return result;
    }

    @Override
    public T get(Long id) {
        return null;//super.getById(id);
    }

    @Override
    public IPage<T> page(long current, long size, T condition) {
        Page<T> page = new Page<>(current, size);
        QueryWrapper<T> wrapper = new QueryWrapper<>(condition);
        // 默认倒序，防止全表无序
        wrapper.orderByDesc("create_time");
        return null;//super.page(page, wrapper);
    }

    @Override
    public List<T> list(T condition) {
        QueryWrapper<T> wrapper = new QueryWrapper<>(condition);
        wrapper.orderByDesc("create_time");
        return null;//super.list(wrapper);
    }

}
