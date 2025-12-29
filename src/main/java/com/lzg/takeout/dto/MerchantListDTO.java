package com.lzg.takeout.dto;

import lombok.Data;

@Data
public class MerchantListDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private Long userId;
}
