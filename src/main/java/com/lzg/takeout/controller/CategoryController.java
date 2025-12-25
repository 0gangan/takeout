package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Category;
import com.lzg.takeout.service.CategoryService;
import com.lzg.takeout.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 添加分类
    @PostMapping
    public R<Category> createCategory(@RequestBody Category category) {
        return R.ok(categoryService.createCategory(category));
    }

    // 获取商家的所有分类（可选功能）
    @GetMapping("/merchant/{merchantId}")
    public R<List<Category>> getCategoriesByMerchant(@PathVariable Long merchantId) {
        return R.ok(categoryService.findByMerchantId(merchantId));
    }
}
