package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public List<Dish> getAll() {
        return dishService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getById(@PathVariable Long id) {
        return dishService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Dish dish) {
        try {
            Dish saved = dishService.create(dish);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Dish updatedDish) {
        return dishService.update(id, updatedDish)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (dishService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
