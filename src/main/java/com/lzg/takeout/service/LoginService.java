package com.lzg.takeout.service;

import com.lzg.takeout.entity.User;

public interface LoginService {
    String login(String username, String password);

    /**
     * Logout by token: revoke token (remove from Redis) and perform any cleanup.
     * @param token the JWT token to revoke (the raw token string without Bearer prefix)
     */
    void logout(String token);
}
