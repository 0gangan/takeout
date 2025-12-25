package com.lzg.takeout.service;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MerchantService {
    MerchantDTO createMerchant(MerchantDTO dto);
    List<MerchantDTO> findAll();
    Optional<MerchantDTO> findById(Long id);
    List<Dish> findDishesByMerchant(Long merchantId);
    Map<String, Long> getTodayOrderCount(Long merchantId);
    Optional<MerchantDTO> updateMerchant(Long id, MerchantDTO dto);
}

