package com.obee.base.controller;

import com.obee.base.model.Order;
import com.obee.base.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/30 11:06
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseDynamicController<OrderService,Order>{

}
