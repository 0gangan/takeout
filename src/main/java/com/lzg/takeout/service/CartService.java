package com.lzg.takeout.service;

import com.lzg.takeout.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findByUserId(Long userId);
    Cart addToCart(Long userId, Long dishId, Integer quantity);
    Cart updateQuantity(Long cartId, Integer quantity);
    boolean deleteById(Long cartId);
}

