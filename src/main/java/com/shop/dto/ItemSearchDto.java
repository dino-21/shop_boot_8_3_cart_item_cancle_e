package com.shop.dto;
import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    //현재 시간과 상품등록일을 비교해서 상품 데이터를 조회한다.
    private String searchDateType;

    // 상품의 판매상태를 기준으로 상품 데이터를 조회한다.
    private ItemSellStatus searchSellStatus;

    // 상품을 조회할때 어떤 유형으로 조회할지 선택한다. itemNum:상품명, createBy:상품 등록자 아이디
    private String searchBy;

    //조회할 검색어 저장할 변수. searchBy가 itemNum일 경우 상품명을 기준으로 검색하고 ,
    // createBy일 경우 상품 등록자 아이디 기준으로 검색한다.
    private String searchQuery="";

}