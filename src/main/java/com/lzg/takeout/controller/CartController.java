package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Cart;
import com.lzg.takeout.service.CartService;
import com.lzg.takeout.util.R;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 获取当前用户购物车
    @GetMapping("/{userId}")
    public R<List<Cart>> getCart(@PathVariable Long userId) {
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            return R.fail(403, "无权限查看此用户的购物车");
        }
        return R.ok(cartService.findByUserId(userId));
    }

    // 添加购物车项
    @PostMapping
    public R<Cart> addToCart(@RequestParam Long userId,
                                       @RequestParam Long dishId,
                                       @RequestParam Integer quantity) {
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            return R.fail(403, "无权限添加到此用户的购物车");
        }
        try {
            Cart saved = cartService.addToCart(userId, dishId, quantity);
            return R.ok(saved);
        } catch (IllegalArgumentException ex) {
            return R.fail(400, ex.getMessage());
        }
    }

    // 修改数量
    @PutMapping("/{cartId}")
    public R<Cart> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
        if (!SecurityUtils.isCurrentUserMerchant()) {
            return R.fail(403, "无权限修改此购物车项");
        }
        try {
            return R.ok(cartService.updateQuantity(cartId, quantity));
        } catch (IllegalArgumentException ex) {
            return R.fail(404, "购物车项不存在");
        }
    }

    // 删除购物车项
    @DeleteMapping("/{cartId}")
    public R<Void> deleteCartItem(@PathVariable Long cartId) {
        if (!SecurityUtils.isCurrentUserMerchant()) {
            return R.fail(403, "无权限删除此购物车项");
        }
        if (cartService.deleteById(cartId)) {
            return R.ok();
        }
        return R.fail(404, "购物车项未找到");
    }
}
