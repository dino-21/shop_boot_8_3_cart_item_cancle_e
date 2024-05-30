package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem extends BaseTimeEntity{
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;

    // 정적 메서드로 장바구니 항목 생성
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem(); // 새로운 장바구니 항목 객체 생성
        cartItem.setCart(cart);  // 장바구니 항목에 장바구니 설정
        cartItem.setItem(item); // 장바구니 항목에 아이템 설정
        cartItem.setCount(count);  // 장바구니 항목에 수량 설정
        return cartItem; // 생성된 장바구니 항목 객체 반환
    }

    // 장바구니 항목의 수량 증가 메서드
    public void addCount(int count){
        // 1 현재 수량에 추가된 수량을 더함
        this.count += count;
    }

    // 장바구니 항목의 수량 업데이트 메서드
    public void updateCount(int count){
        // 장바구니 항목의 수량을 지정된 값으로 업데이트
        this.count = count;
    }
}
