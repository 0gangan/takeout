package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Order;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderRepository;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findByMerchantId(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Order saveOrder(Order order) {
        if (order.getUser() != null) {
            userRepository.findById(order.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
        }
        if (order.getMerchant() != null) {
            merchantRepository.findById(order.getMerchant().getId())
                    .orElseThrow(() -> new RuntimeException("商家不存在"));
        }
        return orderRepository.save(order);
    }

    @Override
    public long countOrdersTodayByMerchantId(Long merchantId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.countByMerchantIdAndOrderTimeBetween(merchantId, startOfDay, endOfDay);
    }
}

