package com.lzg.takeout.service.impl;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.dto.MerchantListDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.DishRepository;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.OrderDetailRepository;
import com.lzg.takeout.repository.OrderRepository;
import com.lzg.takeout.service.MerchantService;
import lombok.RequiredArgsConstructor;
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
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    @Override
    public MerchantDTO createMerchant(MerchantDTO dto) {
        Merchant merchant = toEntity(dto);
        Merchant saved = merchantRepository.save(merchant);
        return toDTO(saved);
    }

    @Override
    public List<MerchantListDTO> findAll() {
        return merchantRepository.findAll().stream().map(this::toListDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<MerchantDTO> findById(Long id) {
        return merchantRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<Dish> findDishesByMerchant(Long merchantId) {
        return dishRepository.findByMerchantId(merchantId);
    }

    @Override
    public Map<String, Long> getTodayOrderCount(Long merchantId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long count = orderRepository.countByMerchantIdAndOrderTimeBetween(merchantId, startOfDay, endOfDay);
        return Collections.singletonMap("count", count);
    }

    @Override
    public Optional<MerchantDTO> updateMerchant(Long id, MerchantDTO dto) {
        return merchantRepository.findById(id).map(merchant -> {
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

    @Override
    public boolean deleteMerchant(Long id) {
        Optional<Merchant> merchantOpt = merchantRepository.findById(id);
        if (merchantOpt.isEmpty()) {
            return false;
        }
        Merchant merchant = merchantOpt.get();
        orderDetailRepository.deleteByOrder_MerchantId(id);
        dishRepository.deleteByMerchantId(id);
        orderRepository.deleteByMerchantId(id);
        merchantRepository.delete(merchant);
        return true;
    }

    private Merchant toEntity(MerchantDTO dto) {
        Merchant merchant = new Merchant();
        merchant.setName(dto.getName());
        merchant.setAddress(dto.getAddress());
        merchant.setPhone(dto.getPhone());
        merchant.setDescription(dto.getDescription());
        merchant.setUser(dto.getUser());
        return merchant;
    }

    private MerchantDTO toDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setAddress(merchant.getAddress());
        dto.setPhone(merchant.getPhone());
        dto.setDescription(merchant.getDescription());
        dto.setUser(merchant.getUser());
        return dto;
    }

    private MerchantListDTO toListDTO(Merchant merchant) {
        MerchantListDTO dto = new MerchantListDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setAddress(merchant.getAddress());
        dto.setPhone(merchant.getPhone());
        dto.setDescription(merchant.getDescription());
        if (merchant.getUser() != null) {
            dto.setUserId(merchant.getUser().getId());
        }
        return dto;
    }
}
