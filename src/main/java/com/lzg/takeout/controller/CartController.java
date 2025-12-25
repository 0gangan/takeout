package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Cart;
import com.lzg.takeout.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 获取当前用户购物车
    @GetMapping("/{userId}")
    public List<Cart> getCart(@PathVariable Long userId) {
        return cartService.findByUserId(userId);
    }

    // 添加购物车项
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                       @RequestParam Long dishId,
                                       @RequestParam Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.addToCart(userId, dishId, quantity));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 修改数量
    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.updateQuantity(cartId, quantity));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // 删除购物车项
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartId) {
        if (cartService.deleteById(cartId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
