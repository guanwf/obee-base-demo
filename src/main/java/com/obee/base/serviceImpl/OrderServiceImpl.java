package com.obee.base.serviceImpl;

import com.obee.base.model.*;
import com.obee.base.service.ChildDataHook;
import com.obee.base.service.OrderService;
import com.obee.base.support.BaseDynamicServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:25
 */
@Service
@ModuleInfo(
        code = "ORDER",
        name = "订单管理",
        children = {
                // 配置子表 1：订单明细
                @ChildModule(
                        key = "OrderItem",                  // 前端传的 JSON key
                        entity = OrderItem.class,       // 实体类
//                        service = OrderItemService.class, // Service 类
                        fkField = "orderId"             // 实体中的外键属性名
                ),
                // 配置子表 2：支付记录
                @ChildModule(
                        key = "OrderPay",
                        entity = OrderPay.class,
//                        service = OrderPayService.class,
                        fkField = "orderId"
                )
        }
)
@Slf4j
public class OrderServiceImpl extends BaseDynamicServiceImpl<Order> implements OrderService {
    public void test(){

        registerChildHook("items", new ChildDataHook<OrderItem>() {
            @Override
            public void beforeAdd(OrderItem item) {

            }
        });

    }

    /**
     * 在 Service 初始化完成后，注册所有的业务规则
     */
    @PostConstruct
    public void initBusinessRules() {
        // 调用基类的注解解析
        super.initChildHandlers();

        // === 1. 定义 "items" (订单明细) 的业务逻辑 ===
        this.registerChildHook("OrderItem", new ChildDataHook<OrderItem>() {
            @Override
            public void beforeAdd(OrderItem item) {

                log.info("{}",item.getGoodsId());
//                if (item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
//                    throw new RuntimeException("新增明细失败：商品价格不能为负数");
//                }
//                // 自动计算小计
//                item.setSubTotal(item.getPrice().multiply(new BigDecimal(item.getCount())));
//                log.info("正在新增明细: {}", item.getProductName());
            }

            @Override
            public void beforeUpdate(OrderItem item) {

                log.info("{}",item.getGoodsId());

                // 修改时的逻辑
//                if (item.getCount() <= 0) {
//                    throw new RuntimeException("修改明细失败：数量必须大于0");
//                }
            }

            @Override
            public void beforeDelete(Long id) {

                log.info("{}",id);

                // 比如：已经发货的明细不能删
                // OrderItem dbItem = orderItemMapper.selectById(id); // 这里甚至可以注入 Mapper 来查库
            }

            @Override
            public void afterAdd(OrderItem item){
                log.info("{}",item.getGoodsId());
            }
        });

        // === 2. 定义 "pays" (支付记录) 的业务逻辑 ===
        this.registerChildHook("OrderPay", new ChildDataHook<OrderPay>() {
            @Override
            public void beforeAdd(OrderPay pay) {

                log.info("{}",pay.getPayId());

//                if (!List.of("ALIPAY", "WECHAT").contains(pay.getPayType())) {
//                    throw new RuntimeException("不支持的支付方式");
//                }
            }
        });
    }

}
