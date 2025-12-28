package com.lzg.takeout.util;

import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * 权限工具类，用于获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    private static UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        SecurityUtils.userRepository = userRepository;
    }

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户的完整信息
     */
    public static User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }

    /**
     * 检查当前用户是否为指定用户或管理员
     */
    public static boolean isCurrentUserOrAdmin(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        // 如果是管理员（假设管理员角色为"ADMIN"），则允许操作
        if ("ADMIN".equals(currentUser.getRole())) {
            return true;
        }
        // 否则只能操作自己的信息
        return currentUser.getId().equals(userId);
    }

    /**
     * 检查当前用户是否为商家
     */
    public static boolean isCurrentUserMerchant() {
        User currentUser = getCurrentUser();
        return currentUser != null && "MERCHANT".equals(currentUser.getRole());
    }

    /**
     * 检查当前用户是否为指定角色
     */
    public static boolean hasRole(String role) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getRole().equals(role);
    }
}