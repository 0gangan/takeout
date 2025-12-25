package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.LoginService;
import com.lzg.takeout.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String login(String username, String password) {
        // 1. 验证用户名是否存在
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("error"));

        //2. 验证密码是否正确
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "error";
        }

        // 3. 生成 token，包含 role
        String token = jwtUtils.generateToken(username, user.getRole());

        // 4. 将 token 存入 redis，设置过期时间 12 小时
        String key = "auth:token:" + token;
        redisTemplate.opsForValue().set(key, username, Duration.ofHours(12));

        // 5. 返回 token
        return token;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        String key = "auth:token:" + token;
        redisTemplate.delete(key);
        // 清理 SecurityContext，以防在同一请求线程中仍有认证信息
        SecurityContextHolder.clearContext();
    }

}
