package com.lzg.takeout.service;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserCrudService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User create(User user);
    Optional<User> update(Long id, User updatedUser);
    boolean deleteById(Long id);
    boolean existsByUsername(String username);
    Optional<Merchant> findMerchantByUserId(Long userId);
}

