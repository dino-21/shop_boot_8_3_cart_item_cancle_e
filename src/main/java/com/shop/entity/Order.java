package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")  //order 키워드가 있으므로 Order 엔티티에 매핑되는 테이블로 orders를 지정
@Getter
@Setter
public class Order extends BaseTimeEntity{
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private  Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;   // 한명의 회원은 여러번 주문할 수 있으므로 주문엔티티 기준 다대일 단방향매핑

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;  //주문상태


    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정한다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    //OrderItem(주문상품) 정보만 가져옴
    private List<OrderItem> orderItems = new ArrayList<>();

//
//
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    // 1 주문 상품을 주문 엔티티에 추가하는 메서드
    //  매개변수(orderItem 추가할 주문 상품)
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); // 주문 상품 리스트에 추가
        orderItem.setOrder(this); // 2 주문 상품에 현재 주문을 설정
    }



    //새로운 주문을 생성하는 정적 메서드
    // 매개변수(member 주문을 하는 회원, orderItemList 주문할 상품 리스트, 생성된 주문 객체)
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member); // 3 주문에 회원 설정

        // 4 상품페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에는 한번에 여러개의 상품을 주문할 수 있다.
        // 여러개의 주문 상품을 담을 수 있도록 리스트 형태로 파라미터 값을 받으며
        // 주문 객체에 orderItem 객체를 추가한다.
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem); // 주문할 상품을 주문에 추가
        }

        order.setOrderStatus(OrderStatus.ORDER); // 5 주문 상태를 'ORDER'로 설정
        order.setOrderDate(LocalDateTime.now()); //6 주문 날짜를 현재 시간으로 설정
        return order; // 생성된 주문 객체 반환
    }


    //7 주문의 총 가격을 계산하는 메서드
    //리턴 주문에 포함된 모든 상품의 총 가격
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice(); // 각 주문 상품의 총 가격을 더함
        }
        return totalPrice;  // 총 가격 반환
    }


    // 주문을 취소하면 주문 상태를 'CANCEL'로 변경하고,
    // 주문에 속한 모든 주문 상품의 취소 메서드를 호출하여 재고를 증가시킵니다
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

}
