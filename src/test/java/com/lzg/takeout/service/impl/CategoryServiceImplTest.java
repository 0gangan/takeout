package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Category;
import com.lzg.takeout.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 分类服务实现类测试
 * 作者：zyr
 * 日期：12.28
 * 功能：测试CategoryServiceImpl中的createCategory和findByMerchantId方法
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository; // 模拟分类数据访问层

    @InjectMocks
    private CategoryServiceImpl categoryService; // 注入待测试的服务实现类

    /**
     * 测试创建分类方法
     * 场景：正常创建分类
     * 验证：返回的分类包含保存后的ID和正确名称，且数据访问层的save方法被调用一次
     */
    @Test
    public void testCreateCategory() {
        // 准备测试数据：待保存的分类和预期保存后的分类
        Category category = new Category();
        category.setName("主食");
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("主食");

        // 模拟数据访问层的save方法行为
        when(categoryRepository.save(category)).thenReturn(savedCategory);

        // 执行待测试的创建方法
        Category result = categoryService.createCategory(category);

        // 验证结果：非空、ID正确、名称正确
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("主食", result.getName());
        // 验证数据访问层的save方法被调用一次
        verify(categoryRepository, times(1)).save(category);
    }

    /**
     * 测试根据商家ID查询分类方法
     * 场景：查询指定商家的分类列表
     * 验证：返回的列表非空、数量正确，且数据访问层的findByMerchantId方法被调用一次
     */
}