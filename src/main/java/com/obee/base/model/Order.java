package com.obee.base.model;

import lombok.Data;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:25
 */
@Data
public class Order extends BaseEntity{
    private String orderId;
    private String orderName;
}
