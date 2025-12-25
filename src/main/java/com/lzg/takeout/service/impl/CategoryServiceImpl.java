package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Category;
import com.lzg.takeout.repository.CategoryRepository;
import com.lzg.takeout.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findByMerchantId(Long merchantId) {
        return categoryRepository.findByMerchantId(merchantId);
    }
}

