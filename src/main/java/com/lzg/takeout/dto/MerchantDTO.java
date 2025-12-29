package com.lzg.takeout.dto;

import com.lzg.takeout.entity.User;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class MerchantDTO {
    private Long id;

    @NotBlank(message = "商家名称不能为空")
    @Size(max = 50, message = "商家名称不能超过50字符")
    private String name;

    private User user;

    @Size(max = 200, message = "地址不能超过200字符")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^[0-9]+$", message = "联系电话只能包含数字")
    @Size(min = 11, max = 11, message = "联系电话必须为11位")
    private String phone;

    @Size(max = 500, message = "描述不能超过500字符")
    private String description;
}
