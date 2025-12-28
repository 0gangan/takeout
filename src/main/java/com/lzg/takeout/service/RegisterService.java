package com.lzg.takeout.service;

public interface RegisterService {
    // Accept role so controller can request BUYER or MERCHANT (null/empty -> BUYER)
    String register(String username, String password, String role);
}
