/**
 * 作者: zyr
 * 日期: 12.28
 *
 * MerchantServiceImpl的单元测试类
 * 重点测试商家信息查询、商家信息更新、商家菜品关联查询逻辑
 */
package com.lzg.takeout.service.impl;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceImplTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    // 测试 createMerchant 方法
    @Test
    void shouldCreateMerchantSuccessfully() {
        // Given
        MerchantDTO dto = new MerchantDTO();
        dto.setName("测试餐厅");
        dto.setAddress("测试地址");
        dto.setPhone("123456789");

        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setName("测试餐厅");
        merchant.setAddress("测试地址");
        merchant.setPhone("123456789");

        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        // When
        MerchantDTO result = merchantService.createMerchant(dto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试餐厅", result.getName());
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    // 测试 findAll 方法
    @Test
    void shouldReturnAllMerchantsAsDTOs() {
        // Given
        Merchant merchant1 = new Merchant();
        merchant1.setId(1L);
        merchant1.setName("餐厅1");
        merchant1.setAddress("地址1");
        merchant1.setPhone("111");

        Merchant merchant2 = new Merchant();
        merchant2.setId(2L);
        merchant2.setName("餐厅2");
        merchant2.setAddress("地址2");
        merchant2.setPhone("222");

        when(merchantRepository.findAll()).thenReturn(Arrays.asList(merchant1, merchant2));

        // When
        List<MerchantDTO> result = merchantService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("餐厅1", result.get(0).getName());
        assertEquals("餐厅2", result.get(1).getName());
        verify(merchantRepository, times(1)).findAll();
    }

    // 测试 findById 方法（存在的情况）
    @Test
    void shouldReturnMerchantDTOWhenIdExists() {
        // Given
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setName("测试餐厅");
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(merchant));

        // When
        Optional<MerchantDTO> result = merchantService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("测试餐厅", result.get().getName());
    }

    // 测试 findById 方法（不存在的情况）
    @Test
    void shouldReturnEmptyWhenIdNotExists() {
        // Given
        when(merchantRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<MerchantDTO> result = merchantService.findById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    // 测试 findDishesByMerchant 方法
    @Test
    void shouldReturnDishesForMerchant() {
        // Given
        Long merchantId = 1L;
        Dish dish1 = new Dish();
        dish1.setId(101L);
        dish1.setName("菜品1");
        Dish dish2 = new Dish();
        dish2.setId(102L);
        dish2.setName("菜品2");

        when(dishRepository.findByMerchantId(merchantId)).thenReturn(Arrays.asList(dish1, dish2));

        // When
        List<Dish> result = merchantService.findDishesByMerchant(merchantId);

        // Then
        assertEquals(2, result.size());
        assertEquals("菜品1", result.get(0).getName());
        verify(dishRepository, times(1)).findByMerchantId(merchantId);
    }

    // 测试 getTodayOrderCount 方法
    @Test
    void shouldReturnTodayOrderCount() {
        // Given
        Long merchantId = 1L;
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        when(orderRepository.countByMerchantIdAndOrderTimeBetween(merchantId, start, end)).thenReturn(10L);

        // When
        Map<String, Long> result = merchantService.getTodayOrderCount(merchantId);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.get("count"));
        verify(orderRepository, times(1)).countByMerchantIdAndOrderTimeBetween(merchantId, start, end);
    }

    // 测试 updateMerchant 方法（存在的情况）
    @Test
    void shouldUpdateMerchantSuccessfully() {
        // Given
        Long merchantId = 1L;
        Merchant existingMerchant = new Merchant();
        existingMerchant.setId(merchantId);
        existingMerchant.setName("旧名称");
        existingMerchant.setDescription("旧描述");
        existingMerchant.setUser(new User()); // 初始化用户信息

        MerchantDTO updateDto = new MerchantDTO();
        updateDto.setName("新名称");
        updateDto.setDescription("新描述");
        User userDto = new User();
        userDto.setUsername("新用户名");
        userDto.setEmail("new@test.com");
        updateDto.setUser(userDto);

        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(existingMerchant));
        when(merchantRepository.save(any(Merchant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<MerchantDTO> result = merchantService.updateMerchant(merchantId, updateDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("新名称", result.get().getName());
        assertEquals("新用户名", existingMerchant.getUser().getUsername());
        assertEquals("new@test.com", existingMerchant.getUser().getEmail());
        verify(merchantRepository, times(1)).save(existingMerchant);
    }

    // 测试 updateMerchant 方法（不存在的情况）
    @Test
    void shouldReturnEmptyWhenUpdateNonExistentMerchant() {
        // Given
        Long merchantId = 999L;
        MerchantDTO updateDto = new MerchantDTO();
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.empty());

        // When
        Optional<MerchantDTO> result = merchantService.updateMerchant(merchantId, updateDto);

        // Then
        assertFalse(result.isPresent());
        verify(merchantRepository, never()).save(any());
    }
}