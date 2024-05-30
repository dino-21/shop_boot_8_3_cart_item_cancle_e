package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
// 사용자에게 주문 이력을 전달하기 위한 DTO
public class OrderHistDto {
    private Long orderId; // 주문 ID
    private String orderDate; // 주문 날짜
    private OrderStatus orderStatus; // 주문 상태

    // 1 Order 엔티티를 받아서 OrderHistDto 객체를 생성하는 생성자
    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.orderStatus = order.getOrderStatus();
    }

    //주문 상품 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    //2 주문 상품 리스트에 OrderItemDto 객체를 추가하는 메서드
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }
}
