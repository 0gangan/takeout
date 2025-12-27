package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * 作者：lzg
 * 日期：2025-12-28
 *
 * LoginServiceImpl 的单元测试类
 * 使用 Mockito 对依赖组件进行 Mock，
 * 重点验证登录与注销的核心业务逻辑是否正确。
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试场景：
     * 用户名和密码正确时登录成功
     *
     * 预期结果：
     * 1. 返回生成的 JWT Token
     * 2. Token 被正确存入 Redis
     */
    @Test
    void login_success_shouldReturnTokenAndStoreInRedis() {
        String username = "alice";
        String rawPassword = "pass";
        String hashed = "hashed-pass";
        String token = "jwt-token";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(hashed);
        user.setRole("BUYER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashed)).thenReturn(true);
        when(jwtUtils.generateToken(username, "BUYER")).thenReturn(token);

        String result = loginService.login(username, rawPassword);

        assertEquals(token, result);

        verify(valueOperations, times(1))
                .set(eq("auth:token:" + token), eq(username), any(Duration.class));
    }

    /**
     * 测试场景：
     * 用户名存在但密码错误
     *
     * 预期结果：
     * 1. 登录失败
     * 2. 返回错误标识
     * 3. 不进行 Redis 写入操作
     */
    @Test
    void login_wrongPassword_returnsError() {
        String username = "bob";
        String rawPassword = "wrong";
        String hashed = "hashed-pass";

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashed);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashed)).thenReturn(false);

        String result = loginService.login(username, rawPassword);

        assertEquals("error", result);
        verify(redisTemplate, never()).opsForValue();
    }

    /**
     * 测试场景：
     * 登录时用户名不存在
     *
     * 预期结果：
     * 抛出运行时异常，表示用户不存在
     */
    @Test
    void login_userNotFound_shouldThrow() {
        when(userRepository.findByUsername("noone")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loginService.login("noone", "any"));
    }

    /**
     * 测试场景：
     * 用户携带合法 Token 执行注销操作
     *
     * 预期结果：
     * 1. Redis 中对应的 Token 被删除
     * 2. SecurityContext 中的认证信息被清空
     */
    @Test
    void logout_withToken_shouldDeleteRedisKeyAndClearSecurityContext() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        String token = "tok";
        when(redisTemplate.delete("auth:token:" + token)).thenReturn(true);

        loginService.logout(token);

        verify(redisTemplate, times(1))
                .delete("auth:token:" + token);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 测试场景：
     * 注销时 Token 为空或为 null
     *
     * 预期结果：
     * 1. 方法正常返回
     * 2. 不进行 Redis 删除操作
     */
    @Test
    void logout_nullOrEmptyToken_shouldDoNothing() {
        loginService.logout(null);
        loginService.logout("");

        verify(redisTemplate, never()).delete(anyString());
    }
}
