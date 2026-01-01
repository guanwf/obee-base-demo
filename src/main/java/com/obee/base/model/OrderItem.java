package com.obee.base.model;

import lombok.Data;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:26
 */
@Data
public class OrderItem extends BaseEntity{
    private String goodsId;
    private Double qty;
}
