package com.lzg.takeout.service;

import com.lzg.takeout.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    List<Category> findByMerchantId(Long merchantId);
}

