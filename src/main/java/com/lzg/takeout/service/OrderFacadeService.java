package com.lzg.takeout.service;

import com.lzg.takeout.dto.OrderDTO;
import com.lzg.takeout.dto.OrderRequest;

import java.util.List;
import java.util.Optional;

public interface OrderFacadeService {
    List<OrderDTO> getOrdersByUserId(Long userId);
    List<OrderDTO> getOrdersByMerchantId(Long merchantId);
    Optional<OrderDTO> getOrderById(Long orderId);
    Long countTodayOrders(Long merchantId);
    void submitOrder(OrderRequest request);
}

