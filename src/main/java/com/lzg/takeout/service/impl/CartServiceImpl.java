package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Cart;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.CartRepository;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    @Override
    public List<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart addToCart(Long userId, Long dishId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new IllegalArgumentException("菜品不存在"));
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setDish(dish);
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateQuantity(Long cartId, Integer quantity) {
        return cartRepository.findById(cartId).map(cart -> {
            cart.setQuantity(quantity);
            return cartRepository.save(cart);
        }).orElseThrow(() -> new IllegalArgumentException("购物车项不存在"));
    }

    @Override
    public boolean deleteById(Long cartId) {
        if (cartRepository.existsById(cartId)) {
            cartRepository.deleteById(cartId);
            return true;
        }
        return false;
    }
}

