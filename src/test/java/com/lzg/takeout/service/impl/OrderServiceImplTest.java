package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.Order;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderRepository;
import com.lzg.takeout.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * 作者：zyr
 * 日期：12.28
 *
 *  OrderServiceImpl的单元测试类
 *重点测试用户 / 商家存在性校验和订单保存逻辑
 */


@ExtendWith(MockitoExtension.class) // 启用Mockito扩展
class OrderServiceImplTest {

    // 模拟依赖的Repository
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MerchantRepository merchantRepository;

    // 注入被测试的Service
    @InjectMocks
    private OrderServiceImpl orderService;

    /**
     * 测试场景：保存订单时用户和商家都存在
     * 预期结果：订单成功保存并返回
     */
    @Test
    void saveOrder_withValidUserAndMerchant_shouldReturnSavedOrder() {
        // 1. 准备测试数据
        Long userId = 1L;
        Long merchantId = 10L;
        Order order = new Order();

        User user = new User();
        user.setId(userId);

        Merchant merchant = new Merchant();
        merchant.setId(merchantId);

        order.setUser(user);
        order.setMerchant(merchant);

        // 2. 配置Mock行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(100L); // 模拟生成ID
            return savedOrder;
        });

        // 3. 执行测试方法
        Order result = orderService.saveOrder(order);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getId()); // 验证ID被设置
        verify(userRepository, times(1)).findById(userId); // 验证用户查询被调用
        verify(merchantRepository, times(1)).findById(merchantId); // 验证商家查询被调用
        verify(orderRepository, times(1)).save(order); // 验证订单保存被调用
    }

    /**
     * 测试场景：保存订单时用户不存在
     * 预期结果：抛出RuntimeException
     */
    @Test
    void saveOrder_withInvalidUser_shouldThrowException() {
        // 1. 准备测试数据
        Long invalidUserId = 999L;
        Order order = new Order();
        User user = new User();
        user.setId(invalidUserId);
        order.setUser(user);

        // 2. 配置Mock行为（用户查询返回空）
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // 3. 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> orderService.saveOrder(order),
                "用户不存在");

        // 4. 验证交互
        verify(userRepository, times(1)).findById(invalidUserId);
        verify(merchantRepository, never()).findById(any()); // 商家查询不应被调用
        verify(orderRepository, never()).save(any()); // 订单保存不应被调用
    }

    /**
     * 测试场景：统计商家当日订单数量
     * 预期结果：返回正确的统计数
     */
    @Test
    void countOrdersTodayByMerchantId_shouldReturnCorrectCount() {
        // 1. 准备测试数据
        Long merchantId = 2L;
        long expectedCount = 5;

        // 2. 配置Mock行为
        when(orderRepository.countByMerchantIdAndOrderTimeBetween(
                eq(merchantId),
                any(),
                any()
        )).thenReturn(expectedCount);

        // 3. 执行测试方法
        long result = orderService.countOrdersTodayByMerchantId(merchantId);

        // 4. 验证结果
        assertEquals(expectedCount, result);
        verify(orderRepository, times(1)).countByMerchantIdAndOrderTimeBetween(
                eq(merchantId),
                any(),
                any()
        );
    }
}