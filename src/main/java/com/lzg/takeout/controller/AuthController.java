package com.lzg.takeout.controller;

import com.lzg.takeout.dto.LoginRequest;
import com.lzg.takeout.dto.LoginResponse;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.LoginService;
import com.lzg.takeout.service.RegisterService;
import com.lzg.takeout.util.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RegisterService registerService;

    Logger logger = Logger.getLogger(AuthController.class.getName());
    /** 登录接口：返回 token + 用户信息（R<LoginResponse>），前端据此跳转 */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<LoginResponse> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String token = loginService.login(username, password);
        if (Objects.equals(token, "error")) {
            return R.fail("用户名或密码错误");
        }

        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return R.fail("用户不存在");
        }
        User user = optionalUser.get();

        LoginResponse resp = new LoginResponse(
                user.getId(),
                token,
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );

        logger.info("用户 " + username + " 登录成功，生成令牌：" + token);
        return R.ok(resp);
    }

    /** 注销：委托给 LoginService.logout */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return R.fail("缺少或非法的 Authorization 头");
        }
        String token = header.substring(7);

        // Delegate to service
        loginService.logout(token);

        return R.ok("注销成功");
    }

    /** 简易注册接口：保存用户并返回 JWT （返回 R<LoginResponse>）*/
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public R<LoginResponse> register(@RequestBody LoginRequest request) {
        try {
            String token = registerService.register(request.getUsername(), request.getPassword());

            Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());
            if (optionalUser.isEmpty()) {
                return R.fail("用户注册后找不到");
            }
            User savedUser = optionalUser.get();

            LoginResponse resp = new LoginResponse(
                    savedUser.getId(),    // 新增id字段
                    token,
                    savedUser.getUsername(),
                    savedUser.getRole(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getAddress()
            );

            return R.ok(resp);
        } catch (RuntimeException ex) {
            return R.fail(ex.getMessage());
        }
    }

}
