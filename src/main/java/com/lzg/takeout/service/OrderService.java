package com.lzg.takeout.service;

import com.lzg.takeout.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    /**
     * 根据用户ID获取订单列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据商家ID获取订单列表
     */
    List<Order> findByMerchantId(Long merchantId);

    /**
     * 根据订单ID查询订单详情
     */
    Optional<Order> findById(Long orderId);

    /**
     * 保存订单（包含订单详情）
     */
    Order saveOrder(Order order);

    long countOrdersTodayByMerchantId(Long merchantId);
}
