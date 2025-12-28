package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * 菜品服务实现类测试
 * 作者：zyr
 * 时间：12.28
 * 功能：测试DishServiceImpl中的findAll、findById、create、update、deleteById方法
 */
@ExtendWith(MockitoExtension.class)
public class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    @Test
    public void testFindAll() {
        // 准备数据
        Dish dish1 = new Dish();
        dish1.setId(1L);
        Dish dish2 = new Dish();
        dish2.setId(2L);
        List<Dish> mockDishes = Arrays.asList(dish1, dish2);

        // 模拟依赖行为
        when(dishRepository.findAll()).thenReturn(mockDishes);

        // 执行测试方法
        List<Dish> result = dishService.findAll();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dishRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        // 准备数据
        Long dishId = 1L;
        Dish mockDish = new Dish();
        mockDish.setId(dishId);
        mockDish.setName("宫保鸡丁");

        // 模拟依赖行为
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(mockDish));

        // 执行测试方法
        Optional<Dish> result = dishService.findById(dishId);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("宫保鸡丁", result.get().getName());
        verify(dishRepository, times(1)).findById(dishId);
    }

    @Test
    public void testCreate_Success() {
        // 准备数据
        Long merchantId = 100L;
        Merchant merchant = new Merchant();
        merchant.setId(merchantId);

        Dish dish = new Dish();
        dish.setName("鱼香肉丝");
        dish.setPrice(new BigDecimal("28.00"));
        Merchant dishMerchant = new Merchant();
        dishMerchant.setId(merchantId);
        dish.setMerchant(dishMerchant);

        Dish savedDish = new Dish();
        savedDish.setId(1L);
        savedDish.setName("鱼香肉丝");
        savedDish.setPrice(new BigDecimal("28.00"));
        savedDish.setMerchant(merchant);

        // 模拟依赖行为
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));
        when(dishRepository.save(dish)).thenReturn(savedDish);

        // 执行测试方法
        Dish result = dishService.create(dish);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(merchant, result.getMerchant());
        verify(merchantRepository, times(1)).findById(merchantId);
        verify(dishRepository, times(1)).save(dish);
    }

    @Test
    public void testCreate_InvalidMerchant_ThrowException() {
        // 准备数据
        Dish dish = new Dish();
        dish.setName("无效菜品");
        Merchant dishMerchant = new Merchant();
        dishMerchant.setId(999L); // 不存在的商家ID
        dish.setMerchant(dishMerchant);

        // 模拟依赖行为
        when(merchantRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试方法并验证异常
        assertThrows(IllegalArgumentException.class, () -> dishService.create(dish));
        verify(merchantRepository, times(1)).findById(999L);
        verify(dishRepository, never()).save(any());
    }

    @Test
    public void testUpdate_Success() {
        // 准备数据
        Long dishId = 1L;
        Dish existingDish = new Dish();
        existingDish.setId(dishId);
        existingDish.setName("旧名称");
        existingDish.setPrice(new BigDecimal("10.00"));

        Dish updatedDish = new Dish();
        updatedDish.setName("新名称");
        updatedDish.setPrice(new BigDecimal("20.00"));

        Dish savedDish = new Dish();
        savedDish.setId(dishId);
        savedDish.setName("新名称");
        savedDish.setPrice(new BigDecimal("20.00"));

        // 模拟依赖行为
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        when(dishRepository.save(existingDish)).thenReturn(savedDish);

        // 执行测试方法
        Optional<Dish> result = dishService.update(dishId, updatedDish);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("新名称", result.get().getName());
        assertEquals(new BigDecimal("20.00"), result.get().getPrice());
        verify(dishRepository, times(1)).findById(dishId);
        verify(dishRepository, times(1)).save(existingDish);
    }

    @Test
    public void testUpdate_NotFound() {
        // 准备数据
        Long dishId = 999L;
        Dish updatedDish = new Dish();
        updatedDish.setName("不存在的菜品");

        // 模拟依赖行为
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        // 执行测试方法
        Optional<Dish> result = dishService.update(dishId, updatedDish);

        // 验证结果
        assertFalse(result.isPresent());
        verify(dishRepository, times(1)).findById(dishId);
        verify(dishRepository, never()).save(any());
    }

    @Test
    public void testDeleteById_Success() {
        // 准备数据
        Long dishId = 1L;

        // 模拟依赖行为
        when(dishRepository.existsById(dishId)).thenReturn(true);
        doNothing().when(dishRepository).deleteById(dishId);

        // 执行测试方法
        boolean result = dishService.deleteById(dishId);

        // 验证结果
        assertTrue(result);
        verify(dishRepository, times(1)).existsById(dishId);
        verify(dishRepository, times(1)).deleteById(dishId);
    }

    @Test
    public void testDeleteById_NotFound() {
        // 准备数据
        Long dishId = 999L;

        // 模拟依赖行为
        when(dishRepository.existsById(dishId)).thenReturn(false);

        // 执行测试方法
        boolean result = dishService.deleteById(dishId);

        // 验证结果
        assertFalse(result);
        verify(dishRepository, times(1)).existsById(dishId);
        verify(dishRepository, never()).deleteById(dishId);
    }
}