package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderDto {
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long ItemId;

    @Min(value = 1 , message = "최소 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 구입 수량은 999개 입니다.")
    private int count;
}
