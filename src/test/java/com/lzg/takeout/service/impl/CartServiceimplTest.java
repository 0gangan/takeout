package com.lzg.takeout.service.impl;

import java.math.BigDecimal;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lzg.takeout.entity.Cart;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.CartRepository;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 单元测试")
class CartServiceimplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Dish testDish;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testDish = new Dish();
        testDish.setId(1L);
        testDish.setName("测试菜品");
        testDish.setPrice(BigDecimal.valueOf(25.0));
        testDish.setAvailable(true);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testDish.setMerchant(null);
        testCart.setDish(testDish);
        testCart.setQuantity(2);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-019
     * 用例名称: 正常查询用户购物车-有购物车项
     * 前置条件: 用户存在且有购物车项
     * 测试步骤: 调用 findByUserId 方法查询用户购物车
     * 预期结果: 返回用户的所有购物车项
     */
    @Test
    @DisplayName("findByUserId - 正常查询用户购物车-有购物车项")
    void findByUserId_withItems_success() {
        List<Cart> expectedCarts = Arrays.asList(testCart);
        when(cartRepository.findByUserId(1L)).thenReturn(expectedCarts);

        List<Cart> result = cartService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCart.getId(), result.get(0).getId());
        assertEquals(testCart.getQuantity(), result.get(0).getQuantity());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-020
     * 用例名称: 正常查询用户购物车-无购物车项
     * 前置条件: 用户存在但没有购物车项
     * 测试步骤: 调用 findByUserId 方法查询用户购物车
     * 预期结果: 返回空列表
     */
    @Test
    @DisplayName("findByUserId - 正常查询用户购物车-无购物车项")
    void findByUserId_noItems_success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<Cart> result = cartService.findByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-021
     * 用例名称: 查询不存在的用户购物车
     * 前置条件: 用户不存在
     * 测试步骤: 调用 findByUserId 方法查询不存在的用户
     * 预期结果: 返回空列表（实现逻辑决定）
     */
    @Test
    @DisplayName("findByUserId - 查询不存在的用户购物车")
    void findByUserId_userNotFound_returnsEmptyList() {
        when(cartRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        List<Cart> result = cartService.findByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartRepository, times(1)).findByUserId(999L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-022
     * 用例名称: 正常添加购物车项
     * 前置条件: 用户和菜品都存在
     * 测试步骤: 调用 addToCart 方法添加购物车项
     * 预期结果: 成功添加并返回购物车项
     */
    @Test
    @DisplayName("addToCart - 正常添加购物车项")
    void addToCart_normalCase_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.addToCart(1L, 1L, 2);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testDish, result.getDish());
        assertEquals(2, result.getQuantity());
        verify(userRepository, times(1)).findById(1L);
        verify(dishRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-023
     * 用例名称: 添加购物车-用户不存在
     * 前置条件: 用户不存在
     * 测试步骤: 调用 addToCart 方法添加购物车项
     * 预期结果: 抛出 IllegalArgumentException 异常
     */
    @Test
    @DisplayName("addToCart - 用户不存在")
    void addToCart_userNotFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.addToCart(999L, 1L, 2)
        );

        assertEquals("用户不存在", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(dishRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-024
     * 用例名称: 添加购物车-菜品不存在
     * 前置条件: 用户存在但菜品不存在
     * 测试步骤: 调用 addToCart 方法添加购物车项
     * 预期结果: 抛出 IllegalArgumentException 异常
     */
    @Test
    @DisplayName("addToCart - 菜品不存在")
    void addToCart_dishNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.addToCart(1L, 999L, 2)
        );

        assertEquals("菜品不存在", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(dishRepository, times(1)).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-025
     * 用例名称: 添加购物车-数量为0
     * 前置条件: 用户和菜品都存在
     * 测试步骤: 调用 addToCart 方法添加数量为0的购物车项
     * 预期结果: 成功添加（根据实现逻辑，数量为0也允许）
     */
    @Test
    @DisplayName("addToCart - 数量为0")
    void addToCart_quantityZero_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.addToCart(1L, 1L, 0);

        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-026
     * 用例名称: 添加购物车-数量为负数
     * 前置条件: 用户和菜品都存在
     * 测试步骤: 调用 addToCart 方法添加数量为负数的购物车项
     * 预期结果: 成功添加（根据实现逻辑，数量为负数也允许）
     */
    @Test
    @DisplayName("addToCart - 数量为负数")
    void addToCart_negativeQuantity_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.addToCart(1L, 1L, -5);

        assertNotNull(result);
        assertEquals(-5, result.getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-027
     * 用例名称: 添加购物车-大数量
     * 前置条件: 用户和菜品都存在
     * 测试步骤: 调用 addToCart 方法添加大数量的购物车项
     * 预期结果: 成功添加
     */
    @Test
    @DisplayName("addToCart - 大数量")
    void addToCart_largeQuantity_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.addToCart(1L, 1L, 1000);

        assertNotNull(result);
        assertEquals(1000, result.getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-028
     * 用例名称: 正常更新购物车数量
     * 前置条件: 购物车项存在
     * 测试步骤: 调用 updateQuantity 方法更新数量
     * 预期结果: 成功更新并返回购物车项
     */
    @Test
    @DisplayName("updateQuantity - 正常更新购物车数量")
    void updateQuantity_normalCase_success() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.updateQuantity(1L, 5);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(cartRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(testCart);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-029
     * 用例名称: 更新购物车-购物车项不存在
     * 前置条件: 购物车项不存在
     * 测试步骤: 调用 updateQuantity 方法更新不存在的购物车项
     * 预期结果: 抛出 IllegalArgumentException 异常
     */
    @Test
    @DisplayName("updateQuantity - 购物车项不存在")
    void updateQuantity_cartNotFound_throwsException() {
        when(cartRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.updateQuantity(999L, 5)
        );

        assertEquals("购物车项不存在", exception.getMessage());
        verify(cartRepository, times(1)).findById(999L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-030
     * 用例名称: 更新购物车-数量为0
     * 前置条件: 购物车项存在
     * 测试步骤: 调用 updateQuantity 方法将数量更新为0
     * 预期结果: 成功更新
     */
    @Test
    @DisplayName("updateQuantity - 数量为0")
    void updateQuantity_quantityZero_success() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.updateQuantity(1L, 0);

        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        verify(cartRepository, times(1)).save(testCart);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-031
     * 用例名称: 更新购物车-数量为负数
     * 前置条件: 购物车项存在
     * 测试步骤: 调用 updateQuantity 方法将数量更新为负数
     * 预期结果: 成功更新（根据实现逻辑）
     */
    @Test
    @DisplayName("updateQuantity - 数量为负数")
    void updateQuantity_negativeQuantity_success() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.updateQuantity(1L, -3);

        assertNotNull(result);
        assertEquals(-3, result.getQuantity());
        verify(cartRepository, times(1)).save(testCart);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-032
     * 用例名称: 正常删除购物车项
     * 前置条件: 购物车项存在
     * 测试步骤: 调用 deleteById 方法删除购物车项
     * 预期结果: 返回 true，删除成功
     */
    @Test
    @DisplayName("deleteById - 正常删除购物车项")
    void deleteById_normalCase_success() {
        when(cartRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cartRepository).deleteById(1L);

        boolean result = cartService.deleteById(1L);

        assertTrue(result);
        verify(cartRepository, times(1)).existsById(1L);
        verify(cartRepository, times(1)).deleteById(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-033
     * 用例名称: 删除购物车-购物车项不存在
     * 前置条件: 购物车项不存在
     * 测试步骤: 调用 deleteById 方法删除不存在的购物车项
     * 预期结果: 返回 false，未删除
     */
    @Test
    @DisplayName("deleteById - 购物车项不存在")
    void deleteById_cartNotFound_returnsFalse() {
        when(cartRepository.existsById(999L)).thenReturn(false);

        boolean result = cartService.deleteById(999L);

        assertFalse(result);
        verify(cartRepository, times(1)).existsById(999L);
        verify(cartRepository, never()).deleteById(anyLong());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-034
     * 用例名称: 删除购物车-多次删除同一项
     * 前置条件: 购物车项存在，第一次删除成功
     * 测试步骤: 第一次调用 deleteById 删除，然后再次调用删除
     * 预期结果: 第一次返回 true，第二次返回 false
     */
    @Test
    @DisplayName("deleteById - 多次删除同一项")
    void deleteById_deleteMultipleTimes_returnsCorrectResults() {
        when(cartRepository.existsById(1L)).thenReturn(true).thenReturn(false);
        doNothing().when(cartRepository).deleteById(1L);

        boolean firstResult = cartService.deleteById(1L);
        boolean secondResult = cartService.deleteById(1L);

        assertTrue(firstResult);
        assertFalse(secondResult);
        verify(cartRepository, times(2)).existsById(1L);
        verify(cartRepository, times(1)).deleteById(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-035
     * 用例名称: 批量查询用户购物车
     * 前置条件: 用户存在且有多个购物车项
     * 测试步骤: 调用 findByUserId 方法查询用户购物车
     * 预期结果: 返回用户的所有购物车项
     */
    @Test
    @DisplayName("findByUserId - 批量查询用户购物车")
    void findByUserId_multipleItems_success() {
        Cart cart1 = new Cart();
        cart1.setId(1L);
        cart1.setUser(testUser);
        testDish.setMerchant(null);
        cart1.setDish(testDish);
        cart1.setQuantity(2);

        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUser(testUser);

        Dish dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("第二个菜品");
        dish2.setPrice(BigDecimal.valueOf(30.0));
        cart2.setDish(dish2);
        cart2.setQuantity(3);

        when(cartRepository.findByUserId(1L)).thenReturn(Arrays.asList(cart1, cart2));

        List<Cart> result = cartService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(3, result.get(1).getQuantity());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-036
     * 用例名称: addToCart-用户ID为空
     * 前置条件: 无
     * 测试步骤: 调用 addToCart 方法，用户ID为空
     * 预期结果: 抛出 IllegalArgumentException 异常
     */
    @Test
    @DisplayName("addToCart - 用户ID为空")
    void addToCart_userIdNull_throwsException() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.addToCart(null, 1L, 2)
        );

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-037
     * 用例名称: 更新购物车-购物车ID为空
     * 前置条件: 无
     * 测试步骤: 调用 updateQuantity 方法，购物车ID为空
     * 预期结果: 抛出异常
     */
    @Test
    @DisplayName("updateQuantity - 购物车ID为空")
    void updateQuantity_cartIdNull_throwsException() {
        when(cartRepository.findById(null)).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> cartService.updateQuantity(null, 5)
        );

        verify(cartRepository, times(1)).findById(null);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-038
     * 用例名称: 删除购物车-购物车ID为空
     * 前置条件: 无
     * 测试步骤: 调用 deleteById 方法，购物车ID为空
     * 预期结果: 返回 false
     */
    @Test
    @DisplayName("deleteById - 购物车ID为空")
    void deleteById_cartIdNull_returnsFalse() {
        when(cartRepository.existsById(null)).thenReturn(false);

        boolean result = cartService.deleteById(null);

        assertFalse(result);
        verify(cartRepository, times(1)).existsById(null);
        verify(cartRepository, never()).deleteById(any());
    }

    /**
     * 时间：2025/12/28
     * 作者：刘忠彬
     * 用例编号: TC-CART-039
     * 用例名称: 添加购物车-菜品不可用
     * 前置条件: 用户存在但菜品不可用
     * 测试步骤: 调用 addToCart 方法添加不可用菜品
     * 预期结果: 成功添加（根据实现逻辑，不检查菜品可用性）
     */
    @Test
    @DisplayName("addToCart - 菜品不可用")
    void addToCart_dishUnavailable_success() {
        testDish.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        Cart result = cartService.addToCart(1L, 1L, 2);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
