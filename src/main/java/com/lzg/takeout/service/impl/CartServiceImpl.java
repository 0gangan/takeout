package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Cart;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.CartRepository;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.CartService;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
        // 权限检查：只能查看自己的购物车或管理员可查看
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            throw new AccessDeniedException("无权查看其他用户的购物车");
        }
        
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart addToCart(Long userId, Long dishId, Integer quantity) {
        // 权限检查：只能为自己添加购物车项
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            throw new AccessDeniedException("无权为其他用户添加购物车项");
        }
        
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
            // 权限检查：只能更新自己的购物车项
            if (!SecurityUtils.isCurrentUserOrAdmin(cart.getUser().getId())) {
                throw new AccessDeniedException("无权更新其他用户的购物车项");
            }
            
            cart.setQuantity(quantity);
            return cartRepository.save(cart);
        }).orElseThrow(() -> new IllegalArgumentException("购物车项不存在"));
    }

    @Override
    public boolean deleteById(Long cartId) {
        Optional<Cart> cartOpt = cartRepository.findById(cartId);
        if (cartOpt.isEmpty()) {
            return false;
        }
        Cart cart = cartOpt.get();
        
        // 权限检查：只能删除自己的购物车项
        if (!SecurityUtils.isCurrentUserOrAdmin(cart.getUser().getId())) {
            throw new AccessDeniedException("无权删除其他用户的购物车项");
        }
        
        cartRepository.deleteById(cartId);
        return true;
    }
}

