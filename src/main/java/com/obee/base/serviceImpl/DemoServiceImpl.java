package com.obee.base.serviceImpl;

import com.obee.base.model.DemoVO;
import com.obee.base.service.DemoService;
import com.obee.base.support.BaseSingleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:28
 */
@Service
@Slf4j
public class DemoServiceImpl extends BaseSingleServiceImpl<DemoVO> implements DemoService {

    @Override
    protected void beforeCreate(DemoVO entity) {
        log.info("entity>{}",entity.getGoodsId());
    }

}
