package com.shop.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private Long id;    // 아이템의 고유 식별자(ID)
    private String itemNm;   // 아이템의 이름
    private Integer price;  // 아이템의 가격
    private String itemDetail; // 아이템의 상세 설명
    private String sellStatCd;   // 아이템의 판매 상태 코드
    private LocalDateTime regTime;  // 아이템의 등록 시간
    private LocalDateTime updateTime;  // 아이템의 업데이트 시간
}
