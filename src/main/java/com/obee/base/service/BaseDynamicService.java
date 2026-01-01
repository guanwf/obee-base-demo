package com.obee.base.service;

import com.obee.base.model.BaseEntity;
import com.obee.base.model.UniversalDTO;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:13
 */
public interface BaseDynamicService <M extends BaseEntity>{
    boolean saveOrUpdateDynamic(UniversalDTO<M> dto);

    void check(String sheetId);

}
