package com.lzg.takeout.service;

import com.lzg.takeout.entity.Dish;

import java.util.List;
import java.util.Optional;

public interface DishService {
    List<Dish> findAll();
    Optional<Dish> findById(Long id);
    Dish create(Dish dish);
    Optional<Dish> update(Long id, Dish updatedDish);
    boolean deleteById(Long id);
}

