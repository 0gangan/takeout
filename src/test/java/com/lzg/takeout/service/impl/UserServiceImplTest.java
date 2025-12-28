package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * 用户服务实现类测试
 * 作者：zyr
 * 时间：12.281
 * 功能：测试UserServiceImpl中的findById和save方法
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testFindById() {
        // 准备数据
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testUser");

        // 模拟依赖行为
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // 执行测试方法
        Optional<User> result = userService.findById(userId);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("testUser", result.get().getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testSave() {
        // 准备数据
        User userToSave = new User();
        userToSave.setUsername("newUser");
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newUser");

        // 模拟依赖行为
        when(userRepository.save(userToSave)).thenReturn(savedUser);

        // 执行测试方法
        User result = userService.save(userToSave);

        // 验证结果
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("newUser", result.getUsername());
        verify(userRepository, times(1)).save(userToSave);
    }
}