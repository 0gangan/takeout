package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.service.DishService;
import com.lzg.takeout.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public R<List<Dish>> getAll() {
        return R.ok(dishService.findAll());
    }

    @GetMapping("/{id}")
    public R<Dish> getById(@PathVariable Long id) {
        return dishService.findById(id)
                .map(R::ok)
                .orElse(R.fail(404, "菜品未找到"));
    }

    @PostMapping
    public R<?> create(@RequestBody Dish dish) {
        try {
            Dish saved = dishService.create(dish);
            return R.ok(saved);
        } catch (IllegalArgumentException ex) {
            return R.fail(400, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody Dish updatedDish) {
        return dishService.update(id, updatedDish)
                .map(R::ok)
                .orElse(R.fail(404, "菜品未找到"));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (dishService.deleteById(id)) {
            return R.ok();
        }
        return R.fail(404, "菜品未找到");
    }
}
