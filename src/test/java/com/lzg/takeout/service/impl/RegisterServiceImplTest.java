package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 作者：lzg
 * 日期：2025-12-28
 *
 * RegisterServiceImpl 的单元测试类
 * 主要用于验证用户注册流程中的核心业务逻辑，
 * 包括正常注册及重复用户名等异常场景。
 */
@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private RegisterServiceImpl registerService;

    @BeforeEach
    void setUp() {
    }

    /**
     * 测试场景：
     * 用户名不存在且注册参数合法时进行注册
     *
     * 预期结果：
     * 1. 用户信息被成功保存
     * 2. 注册完成后自动执行登录
     * 3. 返回登录生成的 JWT Token
     */
    @Test
    void register_success_shouldSaveUserAndReturnToken() {
        String username = "newuser";
        String raw = "pwd";
        String hashed = "hashed";
        String token = "tok";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(raw)).thenReturn(hashed);
        when(loginService.login(username, raw)).thenReturn(token);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        String result = registerService.register(username, raw);

        assertEquals(token, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * 测试场景：
     * 注册时用户名已存在
     *
     * 预期结果：
     * 抛出运行时异常，阻止重复注册
     */
    @Test
    void register_existingUsername_shouldThrow() {
        when(userRepository.findByUsername("exists"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class,
                () -> registerService.register("exists", "p"));
    }
}
