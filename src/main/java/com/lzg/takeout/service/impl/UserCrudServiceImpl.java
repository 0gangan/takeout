package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.UserCrudService;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCrudServiceImpl implements UserCrudService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;



    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> update(Long id, User updatedUser) {
        // 权限检查：只能修改自己的信息或管理员可修改
        if (!SecurityUtils.isCurrentUserOrAdmin(id)) {
            Logger logger = Logger.getLogger(UserCrudServiceImpl.class.getName());
            logger.info("无权修改其他用户信息");
            throw new AccessDeniedException("无权修改其他用户信息");
        }
        
        return userRepository.findById(id).map(user -> {
            // Only update fields that are provided (non-null/non-empty)
            if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank()) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhone() != null && !updatedUser.getPhone().isBlank()) {
                user.setPhone(updatedUser.getPhone());
            }
            if (updatedUser.getAddress() != null && !updatedUser.getAddress().isBlank()) {
                user.setAddress(updatedUser.getAddress());
            }
            // Note: password and role should be changed via dedicated endpoints with proper checks
            return userRepository.save(user);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        // 权限检查：只能删除自己的信息或管理员可删除
        if (!SecurityUtils.isCurrentUserOrAdmin(id)) {
            throw new AccessDeniedException("无权删除其他用户信息");
        }
        
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public Optional<Merchant> findMerchantByUserId(Long userId) {
        // 权限检查：只能查看自己的商家信息或管理员可查看
        if (!SecurityUtils.isCurrentUserOrAdmin(userId)) {
            throw new AccessDeniedException("无权查看其他用户的商家信息");
        }
        
        return merchantRepository.findByUserId(userId);
    }
}
