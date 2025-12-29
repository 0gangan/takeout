package com.lzg.takeout.controller;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.service.UserCrudService;
import com.lzg.takeout.util.R;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCrudService userService;

    // 获取所有用户
    @GetMapping
    public R<List<User>> getAllUsers() {
        return R.ok(userService.findAll());
    }

    // 根据 ID 获取用户
    @GetMapping("/{id}")
    public R<User> getUserById(@PathVariable Long id) {
        return userService.findById(id).map(R::ok).orElse(R.fail(404, "用户未找到"));
    }

    // 创建用户（用于注册）
    @PostMapping
    public R<User> createUser(@RequestBody User user) {
        return R.ok(userService.create(user));
    }

    // 更新用户信息（包括地址）
    @PutMapping("/{id}")
    public R<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        if (!SecurityUtils.isCurrentUserOrAdmin(id)) {
            return R.fail(403, "无权限修改此用户的信息");
        }
        return userService.update(id, updatedUser).map(R::ok).orElse(R.fail(404, "用户未找到"));
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        if (!SecurityUtils.isCurrentUserOrAdmin(id)) {
            return R.fail(403, "无权限删除此用户的信息");
        }
        if (!userService.deleteById(id)) {
            return R.fail(404, "用户未找到");
        }
        return R.ok();
    }

    // 检查用户名是否已存在
    @GetMapping("/exists")
    public R<Boolean> checkUserExists(@RequestParam String username) {
        return R.ok(userService.existsByUsername(username));
    }
    @GetMapping("/{userId}/merchant")
    public R<Merchant> getMerchantByUserId(@PathVariable Long userId) {
        return userService.findMerchantByUserId(userId).map(R::ok).orElse(R.fail(404, "商家未找到"));
    }
}
