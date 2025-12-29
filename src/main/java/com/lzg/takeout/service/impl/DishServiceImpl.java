package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.service.DishService;
import lombok.RequiredArgsConstructor;
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
        dish.setMerchant(merchantOpt.get());
        return dishRepository.save(dish);
    }

    @Override
    public Optional<Dish> update(Long id, Dish updatedDish) {
        return dishRepository.findById(id).map(dish -> {
            if (updatedDish.getName() != null) dish.setName(updatedDish.getName());
            if (updatedDish.getPrice() != null) dish.setPrice(updatedDish.getPrice());
            if (updatedDish.getDescription() != null) dish.setDescription(updatedDish.getDescription());
            return dishRepository.save(dish);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        if (dishRepository.existsById(id)) {
            dishRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

