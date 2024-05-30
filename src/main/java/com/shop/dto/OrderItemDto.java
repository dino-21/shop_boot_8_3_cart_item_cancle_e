package com.shop.dto;

import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

// 주문 항목에 대한 데이터 전송 객체 (DTO) 클래스.
@Setter
@Getter
public class OrderItemDto {
    private String itemNm;     // 상품 이름
    private int count;  // 주문 수량
    private int orderPrice; // 주문 가격
    private String imgUrl;  //상품 이미지 경로

    /**
     * OrderItem 객체와 이미지 경로를 사용하여 OrderItemDto 객체를 생성합니다.
     * @param orderItem 주문 항목 엔티티 객체
     * @param imgUrl 상품 이미지 경로
     */
    //1  OrderItemDto클래스의 생성자로 OrderItem 객체와 이미지 경로를 파라미터로 받아서 멤버 변수 값을 세팅한다.
    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemNm = orderItem.getItem().getItemNm(); // OrderItem 엔티티 객체로부터 상품 이름을 설정
        this.count = orderItem.getCount();   // OrderItem 엔티티 객체로부터 주문 수량을 설정
        this.orderPrice = orderItem.getTotalPrice(); // OrderItem 엔티티 객체로부터 총 주문 가격을 설정
        this.imgUrl = imgUrl; // 전달된 이미지 경로를 설정

    }
}
