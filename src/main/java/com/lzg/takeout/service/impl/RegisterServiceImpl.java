package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.LoginService;
import com.lzg.takeout.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;

    @Override
    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("BUYER");
        user.setEmail("");
        user.setPhone("");

        userRepository.save(user);

        // 生成 token（loginService 会把 token 写入 Redis）
        String token = loginService.login(username, password);
        return token;
    }
}
