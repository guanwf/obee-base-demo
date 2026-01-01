package com.obee.base.controller;

import com.obee.base.model.BaseEntity;
import com.obee.base.model.UniversalDTO;
import com.obee.base.service.BaseDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/30 11:03
 */
public abstract class BaseDynamicController<S extends BaseDynamicService<T>, T extends BaseEntity> {

    @Autowired
    protected S baseService;

    @PostMapping("/save")
    public void save(@RequestBody UniversalDTO<T> data) {
        baseService.saveOrUpdateDynamic(data);
    }
}
