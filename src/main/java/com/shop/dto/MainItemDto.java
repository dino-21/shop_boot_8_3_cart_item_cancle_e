package com.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id; // 아이템 ID

    private String itemNm; // 아이템 이름

    private String itemDetail; // 아이템 상세 설명

    private String imgUrl;  // 아이템 이미지 URL

    private Integer price; // 아이템 가격

    /*
        @QueryProjection :
        new QMainItemDto() 생성한 클래스를 => MainItemDto() 변환
     */
    // QueryDSL을 사용하여 쿼리 결과를 이 생성자를 통해 객체로 변환
    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl,Integer price){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
