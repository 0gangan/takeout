package com.lzg.takeout.controller;

import com.lzg.takeout.dto.OrderDTO;
import com.lzg.takeout.dto.OrderRequest;
import com.lzg.takeout.service.OrderFacadeService;
import com.lzg.takeout.util.R;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    // 买家根据用户ID获取订单列表
    @GetMapping("/user/{userId}")
    public R<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            return R.fail(403, "无权限查看此用户的订单");
        }
        return R.ok(orderFacadeService.getOrdersByUserId(userId));
    }

    // 商家根据商家ID获取订单列表
    @GetMapping("/merchant/{merchantId}")
    public R<List<OrderDTO>> getOrdersByMerchantId(@PathVariable Long merchantId) {
        if (!SecurityUtils.isCurrentUserMerchant()) {
            return R.fail(403, "无权限查看此商家的订单");
        }
        return R.ok(orderFacadeService.getOrdersByMerchantId(merchantId));
    }

    @GetMapping("/{orderId}")
    public R<OrderDTO> getOrderDetailById(@PathVariable Long orderId) {
        return orderFacadeService.getOrderById(orderId)
                .map(R::ok)
                .orElse(R.fail(404, "订单未找到"));
    }

    // 获取今日订单数
    @GetMapping("/merchant/{merchantId}/today-count")
    public R<Long> countTodayOrders(@PathVariable Long merchantId) {
        return R.ok(orderFacadeService.countTodayOrders(merchantId));
    }

    // 提交订单
    @PostMapping
    public R<String> submitOrder(@RequestBody OrderRequest orderRequest) {
        orderFacadeService.submitOrder(orderRequest);
        return R.ok("订单提交成功！");
    }
}
