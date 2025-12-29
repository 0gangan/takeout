package com.lzg.takeout.service.impl;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderRepository;
import com.lzg.takeout.service.MerchantService;
import com.lzg.takeout.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;

    @Override
    public MerchantDTO createMerchant(MerchantDTO dto) {
        Merchant merchant = toEntity(dto);
        Merchant saved = merchantRepository.save(merchant);
        return toDTO(saved);
    }

    @Override
    public List<MerchantDTO> findAll() {
        return merchantRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<MerchantDTO> findById(Long id) {
        return merchantRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<Dish> findDishesByMerchant(Long merchantId) {
        // 权限检查：只能查看自己的商家菜品或管理员可查看
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("无效的商家ID"));
        
        if (!SecurityUtils.isCurrentUserOrAdmin(merchant.getUser().getId())) {
            throw new AccessDeniedException("无权查看其他商家的菜品信息");
        }
        
        return dishRepository.findByMerchantId(merchantId);
    }

    @Override
    public Map<String, Long> getTodayOrderCount(Long merchantId) {
        // 权限检查：只能查看自己的商家订单统计或管理员可查看
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("无效的商家ID"));
        
        if (!SecurityUtils.isCurrentUserOrAdmin(merchant.getUser().getId())) {
            throw new AccessDeniedException("无权查看其他商家的订单统计信息");
        }
        
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long count = orderRepository.countByMerchantIdAndOrderTimeBetween(merchantId, startOfDay, endOfDay);
        return Collections.singletonMap("count", count);
    }

    @Override
    public Optional<MerchantDTO> updateMerchant(Long id, MerchantDTO dto) {
        return merchantRepository.findById(id).map(merchant -> {
            // 权限检查：只能更新自己的商家信息或管理员可更新
            if (!SecurityUtils.isCurrentUserOrAdmin(merchant.getUser().getId())) {
                throw new AccessDeniedException("无权更新其他商家的信息");
            }
            
            merchant.setName(dto.getName());
            merchant.setDescription(dto.getDescription());
            if (merchant.getUser() != null && dto.getUser() != null) {
                User user = merchant.getUser();
                user.setUsername(dto.getUser().getUsername());
                user.setEmail(dto.getUser().getEmail());
                user.setPhone(dto.getUser().getPhone());
                user.setAddress(dto.getUser().getAddress());
            }
            Merchant saved = merchantRepository.save(merchant);
            return toDTO(saved);
        });
    }

    private Merchant toEntity(MerchantDTO dto) {
        Merchant merchant = new Merchant();
        merchant.setId(dto.getId());
        merchant.setName(dto.getName());
        merchant.setAddress(dto.getAddress());
        merchant.setPhone(dto.getPhone());
        return merchant;
    }

    private MerchantDTO toDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setAddress(merchant.getAddress());
        dto.setPhone(merchant.getPhone());
        return dto;
    }
}

