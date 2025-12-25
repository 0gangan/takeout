package com.lzg.takeout.controller;

import com.lzg.takeout.dto.MerchantDTO;
import com.lzg.takeout.entity.Dish;
import com.lzg.takeout.service.MerchantService;
import com.lzg.takeout.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    // 添加商家
    @PostMapping
    public R<MerchantDTO> createMerchant(@RequestBody MerchantDTO merchantDTO) {
        return R.ok(merchantService.createMerchant(merchantDTO));
    }

    // 获取所有商家
    @GetMapping
    public R<List<MerchantDTO>> getAllMerchants() {
        return R.ok(merchantService.findAll());
    }

    // 根据 ID 获取商家
    @GetMapping("/{id}")
    public R<MerchantDTO> getMerchantById(@PathVariable Long id) {
        return merchantService.findById(id)
                .map(R::ok)
                .orElse(R.fail(404, "商家未找到"));
    }

    // 获取指定商家的菜品列表（保留 Dish 实体）
    @GetMapping("/{merchantId}/dishes")
    public R<List<Dish>> getDishesByMerchant(@PathVariable Long merchantId) {
        return R.ok(merchantService.findDishesByMerchant(merchantId));
    }

    // 获取当天订单数量
    @GetMapping("/{merchantId}/orders/today-count")
    public R<Map<String, Long>> getTodayOrderCount(@PathVariable Long merchantId) {
        return R.ok(merchantService.getTodayOrderCount(merchantId));
    }

    @PutMapping("/{id}")
    public R<MerchantDTO> updateMerchant(@PathVariable Long id, @RequestBody MerchantDTO merchantDTO) {
        return merchantService.updateMerchant(id, merchantDTO)
                .map(R::ok)
                .orElse(R.fail(404, "商家未找到"));
    }

}
