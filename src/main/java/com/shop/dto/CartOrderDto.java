package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//장바구니 주문 DTO의 리스트를 저장
public class CartOrderDto {
    // 장바구니 아이템의 ID
    private Long cartItemId;

    // 1 장바구니에서 여러 개의 상품을 주문하므로 CartOrderDto 클래스가 자신을 List로 가지고 있도록 만든다.
    private List<CartOrderDto> cartOrderDtoList;
}
