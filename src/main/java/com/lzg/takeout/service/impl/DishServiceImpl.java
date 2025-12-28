package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.service.DishService;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    @Override
    public Dish create(Dish dish) {
        if (dish.getMerchant() == null || dish.getMerchant().getId() == null) {
            throw new IllegalArgumentException("商家信息不完整");
        }
        Optional<Merchant> merchantOpt = merchantRepository.findById(dish.getMerchant().getId());
        if (merchantOpt.isEmpty()) {
            throw new IllegalArgumentException("无效的商家");
        }
        Merchant merchant = merchantOpt.get();
        
        // 权限检查：只能为自己的商家创建菜品
        if (!SecurityUtils.isCurrentUserOrAdmin(merchant.getUser().getId())) {
            throw new AccessDeniedException("无权为其他商家创建菜品");
        }
        
        dish.setMerchant(merchant);
        return dishRepository.save(dish);
    }

    @Override
    public Optional<Dish> update(Long id, Dish updatedDish) {
        return dishRepository.findById(id).map(dish -> {
            // 权限检查：只能更新自己商家的菜品
            if (!SecurityUtils.isCurrentUserOrAdmin(dish.getMerchant().getUser().getId())) {
                throw new AccessDeniedException("无权更新其他商家的菜品");
            }
            
            if (updatedDish.getName() != null) dish.setName(updatedDish.getName());
            if (updatedDish.getPrice() != null) dish.setPrice(updatedDish.getPrice());
            if (updatedDish.getDescription() != null) dish.setDescription(updatedDish.getDescription());
            return dishRepository.save(dish);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<Dish> dishOpt = dishRepository.findById(id);
        if (dishOpt.isEmpty()) {
            return false;
        }
        Dish dish = dishOpt.get();
        
        // 权限检查：只能删除自己商家的菜品
        if (!SecurityUtils.isCurrentUserOrAdmin(dish.getMerchant().getUser().getId())) {
            throw new AccessDeniedException("无权删除其他商家的菜品");
        }
        
        dishRepository.deleteById(id);
        return true;
    }
}

