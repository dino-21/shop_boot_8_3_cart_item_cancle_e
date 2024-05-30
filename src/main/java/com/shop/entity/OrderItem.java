package com.shop.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseTimeEntity{

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    //하나의 상품은 여러 주문 상품으로 들어갈 수 있다.
    //주문상품 기준으로 다대일 단방향 매핑 설정


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    // 한번의 주문에 여러개의 상품을 주문할 수 있다.
    //주문상품 엔티티를 기준으로  다대일 단방향 매핑 설정

    private int orderPrice;  //주문가격

    private int count;  //수량
//
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;


    //주문 상품을 생성하는 정적 메서드  매개변수( item 주문할 상품, count 주문할 수량 )
    //리턴 생성된 주문 상품 객체
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);  // 1 주문 상품 설정
        orderItem.setCount(count); // 2 주문 수량 설정
        orderItem.setOrderPrice(item.getPrice()); // 3 주문 가격 설정 (상품의 가격으로)
        item.removeStock(count);  //4 주문한 수량만큼 재고 감소
        return orderItem;  // 생성된 주문 상품 반환
    }


    //5 주문 상품의 총 가격을 계산하는 메서드
    //리턴 주문 가격과 수량을 곱한 값 (총 가격)
    public int getTotalPrice(){
        return orderPrice*count;  // 주문 가격 * 주문 수량
    }

    // 주문을 취소하면 해당 주문 상품의 수량만큼 재고를 증가
    public void cancel() {
        this.getItem().addStock(count);
    }

}
