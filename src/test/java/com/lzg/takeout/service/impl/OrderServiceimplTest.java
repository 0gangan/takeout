package com.lzg.takeout.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.Order;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderRepository;
import com.lzg.takeout.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceimplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private User testUser;
    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setName("TestRestaurant");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setMerchant(testMerchant);
        testOrder.setTotalAmount(BigDecimal.valueOf(99.0));
        testOrder.setStatus("待付款");
        testOrder.setAddress("北京市朝阳区xxx路xxx号");
        testOrder.setOrderTime(LocalDateTime.now());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-012
     * 用例名称: OrderService.getOrdersByUserId-正常情况
     */
    @Test
    void getOrdersByUserId_normalCase() {
        List<Order> expectedOrderList = Arrays.asList(testOrder);
        when(orderRepository.findByUserId(1L)).thenReturn(expectedOrderList);

        List<Order> result = orderService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByUserId(1L);
    }

    /**
     * 时间：2025/12/28
     ** 作者：刘忠彬
     * 用例编号: ORD-013
     * 用例名称: OrderService.getOrdersByUserId-用户无订单
     */
    @Test
    void getOrdersByUserId_noOrders() {
        when(orderRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.findByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository).findByUserId(999L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-014
     * 用例名称: OrderService.getOrdersByMerchantId-正常情况
     */
    @Test
    void getOrdersByMerchantId_normalCase() {
        List<Order> expectedOrderList = Arrays.asList(testOrder);
        when(orderRepository.findByMerchantId(1L)).thenReturn(expectedOrderList);

        List<Order> result = orderService.findByMerchantId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder, result.get(0));
        verify(orderRepository).findByMerchantId(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-015
     * 用例名称: OrderService.getOrdersByMerchantId-商家无订单
     */
    @Test
    void getOrdersByMerchantId_noOrders() {
        when(orderRepository.findByMerchantId(999L)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.findByMerchantId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository).findByMerchantId(999L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-016
     * 用例名称: OrderService.getOrderById-正常情况
     */
    @Test
    void getOrderById_normalCase() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<Order> result = orderService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(orderRepository).findById(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-017
     * 用例名称: OrderService.getOrderById-订单不存在
     */
    @Test
    void getOrderById_notFound() {
        when(orderRepository.findById(9999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.findById(9999L);

        assertFalse(result.isPresent());
        verify(orderRepository).findById(9999L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-018
     * 用例名称: OrderService.countTodayOrders-正常情况
     */
    @Test
    void countTodayOrders_normalCase() {
        when(orderRepository.countByMerchantIdAndOrderTimeBetween(anyLong(), any(), any()))
                .thenReturn(5L);

        long result = orderService.countOrdersTodayByMerchantId(1L);

        assertEquals(5L, result);
        verify(orderRepository).countByMerchantIdAndOrderTimeBetween(eq(1L), any(), any());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-019
     * 用例名称: OrderService.countTodayOrders-无今日订单
     */
    @Test
    void countTodayOrders_noOrdersToday() {
        when(orderRepository.countByMerchantIdAndOrderTimeBetween(anyLong(), any(), any()))
                .thenReturn(0L);

        long result = orderService.countOrdersTodayByMerchantId(999L);

        assertEquals(0L, result);
        verify(orderRepository).countByMerchantIdAndOrderTimeBetween(eq(999L), any(), any());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-020
     * 用例名称: OrderService.saveOrder-正常保存
     */
    @Test
    void saveOrder_normalCase() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(merchantRepository.findById(1L)).thenReturn(Optional.of(testMerchant));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        Order result = orderService.saveOrder(testOrder);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(merchantRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-021
     * 用例名称: OrderService.saveOrder-用户不存在
     */
    @Test
    void saveOrder_userNotFound() {
        testOrder.getUser().setId(999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(testOrder)
        );

        assertEquals("用户不存在", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: ORD-022
     * 用例名称: OrderService.saveOrder-商家不存在
     */
    @Test
    void saveOrder_merchantNotFound() {
        testOrder.getMerchant().setId(999L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(merchantRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.saveOrder(testOrder)
        );

        assertEquals("商家不存在", exception.getMessage());
        verify(merchantRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
