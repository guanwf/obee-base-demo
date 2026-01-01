package com.obee.base.model;

import lombok.Data;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:27
 */
@Data
public class OrderPay extends BaseEntity{
    private String payId;
    private Double qty;
}
