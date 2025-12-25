package com.lzg.takeout.controller;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    // 添加商家
    @PostMapping
    public ResponseEntity<MerchantDTO> createMerchant(@RequestBody MerchantDTO merchantDTO) {
        return ResponseEntity.ok(merchantService.createMerchant(merchantDTO));
    }

    // 获取所有商家
    @GetMapping
    public ResponseEntity<List<MerchantDTO>> getAllMerchants() {
        return ResponseEntity.ok(merchantService.findAll());
    }

    // 根据 ID 获取商家
    @GetMapping("/{id}")
    public ResponseEntity<MerchantDTO> getMerchantById(@PathVariable Long id) {
        Optional<MerchantDTO> dto = merchantService.findById(id);
        return dto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 获取指定商家的菜品列表（保留 Dish 实体）
    @GetMapping("/{merchantId}/dishes")
    public ResponseEntity<List<Dish>> getDishesByMerchant(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.findDishesByMerchant(merchantId));
    }

    // 获取当天订单数量
    @GetMapping("/{merchantId}/orders/today-count")
    public ResponseEntity<Map<String, Long>> getTodayOrderCount(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.getTodayOrderCount(merchantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MerchantDTO> updateMerchant(@PathVariable Long id, @RequestBody MerchantDTO merchantDTO) {
        return merchantService.updateMerchant(id, merchantDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
