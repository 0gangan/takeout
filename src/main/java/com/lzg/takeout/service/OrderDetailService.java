package com.lzg.takeout.service;

import com.lzg.takeout.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail save(OrderDetail orderDetail);

    List<OrderDetail> saveAll(List<OrderDetail> details);

    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByMerchantIdAndUserId(Long merchantId, Long userId);
}
