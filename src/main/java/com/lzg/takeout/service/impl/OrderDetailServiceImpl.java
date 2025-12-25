package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.OrderDetail;
import com.lzg.takeout.repository.OrderDetailRepository;
import com.lzg.takeout.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> saveAll(List<OrderDetail> details) {
        return orderDetailRepository.saveAll(details);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> findByMerchantIdAndUserId(Long merchantId, Long userId) {
        return orderDetailRepository.findByOrder_Merchant_IdAndOrder_User_Id(merchantId, userId);
    }
}

