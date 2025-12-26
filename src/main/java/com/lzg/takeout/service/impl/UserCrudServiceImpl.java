package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.UserRepository;
import com.lzg.takeout.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        return merchantRepository.findByUserId(userId);
    }
}
