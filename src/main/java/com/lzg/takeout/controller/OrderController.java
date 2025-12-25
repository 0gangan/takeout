package com.lzg.takeout.controller;

import com.lzg.takeout.dto.OrderDTO;
import com.lzg.takeout.dto.OrderRequest;
import com.lzg.takeout.service.OrderFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    // 买家根据用户ID获取订单列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderFacadeService.getOrdersByUserId(userId));
    }

    // 商家根据商家ID获取订单列表
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(orderFacadeService.getOrdersByMerchantId(merchantId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetailById(@PathVariable Long orderId) {
        return orderFacadeService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 获取今日订单数
    @GetMapping("/merchant/{merchantId}/today-count")
    public ResponseEntity<Long> countTodayOrders(@PathVariable Long merchantId) {
        return ResponseEntity.ok(orderFacadeService.countTodayOrders(merchantId));
    }

    // 提交订单
    @PostMapping
    public ResponseEntity<String> submitOrder(@RequestBody OrderRequest orderRequest) {
        orderFacadeService.submitOrder(orderRequest);
        return ResponseEntity.ok("订单提交成功！");
    }
}
