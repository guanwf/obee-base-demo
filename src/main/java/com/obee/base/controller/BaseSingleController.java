package com.obee.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.obee.base.common.Result;
import com.obee.base.model.BaseEntity;
import com.obee.base.service.BaseSingleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:12
 */
@Slf4j
public abstract class BaseSingleController<S extends BaseSingleService<T>, T extends BaseEntity> {

    @Autowired
    protected S baseService;

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody T entity) {
        // 调用自定义的 create 方法 (包含 Hook 逻辑)
        return Result.success(baseService.create(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody T entity) {
        // 调用自定义的 modify 方法 (包含 Hook 逻辑)
        return Result.success(baseService.modify(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(baseService.remove(id));
    }

    @GetMapping("/{id}")
    public Result<T> get(@PathVariable Long id) {
        return Result.success(baseService.get(id));
    }

    @PostMapping("/page")
    public Result<IPage<T>> page(@RequestParam(defaultValue = "1") long current,
                                 @RequestParam(defaultValue = "10") long size,
                                 @RequestBody(required = false) T entity) {
        return Result.success(baseService.page(current, size, entity));
    }
}
