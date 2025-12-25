package com.lzg.takeout.service.impl;

import com.lzg.takeout.dto.*;
import com.lzg.takeout.entity.*;
import com.lzg.takeout.repository.*;
import com.lzg.takeout.service.OrderFacadeService;
import com.lzg.takeout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderService.findByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByMerchantId(Long merchantId) {
        return orderService.findByMerchantId(merchantId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long orderId) {
        return orderService.findById(orderId).map(this::convertToDTO);
    }

    @Override
    public Long countTodayOrders(Long merchantId) {
        return orderService.countOrdersTodayByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void submitOrder(OrderRequest orderRequest) {
        Order order = new Order();

        order.setTotalAmount(orderRequest.getOrder().getTotalAmount());
        order.setAddress(orderRequest.getOrder().getAddress());
        order.setStatus(orderRequest.getOrder().getStatus());
        order.setOrderTime(LocalDateTime.now());

        User user = new User();
        user.setId(orderRequest.getOrder().getUser().getId());
        order.setUser(user);

        Merchant merchant = new Merchant();
        merchant.setId(orderRequest.getOrder().getMerchant().getId());
        order.setMerchant(merchant);

        List<OrderDetail> detailList = orderRequest.getDetails().stream().map(detailDTO -> {
            OrderDetail detail = new OrderDetail();
            detail.setQuantity(detailDTO.getQuantity());
            detail.setPrice(detailDTO.getPrice());

            Dish dish = new Dish();
            dish.setId(detailDTO.getDish().getId());
            detail.setDish(dish);

            detail.setOrder(order);
            return detail;
        }).collect(Collectors.toList());

        order.setDetails(detailList);

        orderService.saveOrder(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus());
        dto.setOrderTime(order.getOrderTime());

        if (order.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setUsername(order.getUser().getUsername());
            dto.setUser(userDTO);
        }

        if (order.getMerchant() != null) {
            MerchantDTO merchantDTO = new MerchantDTO();
            merchantDTO.setId(order.getMerchant().getId());
            merchantDTO.setName(order.getMerchant().getName());
            merchantDTO.setAddress(order.getMerchant().getAddress());
            dto.setMerchant(merchantDTO);
        }

        List<OrderDetailDTO> details = order.getDetails().stream().map(detail -> {
            OrderDetailDTO detailDTO = new OrderDetailDTO();
            detailDTO.setId(detail.getId());
            detailDTO.setQuantity(detail.getQuantity());
            detailDTO.setPrice(detail.getPrice());

            if (detail.getDish() != null) {
                DishDTO dishDTO = new DishDTO();
                dishDTO.setId(detail.getDish().getId());
                dishDTO.setName(detail.getDish().getName());
                dishDTO.setPrice(detail.getDish().getPrice());
                dishDTO.setDescription(detail.getDish().getDescription());
                dishDTO.setImageUrl(detail.getDish().getImageUrl());
                detailDTO.setDish(dishDTO);
            }
            return detailDTO;
        }).collect(Collectors.toList());

        dto.setDetails(details);
        return dto;
    }
}
