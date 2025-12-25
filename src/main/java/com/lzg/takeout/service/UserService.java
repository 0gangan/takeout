package com.lzg.takeout.service;

import com.lzg.takeout.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

    User save(User user);

}
