package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart extends BaseTimeEntity{
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;


    // 정적 메서드로 장바구니 생성
    public static Cart createCart(Member member){
        Cart cart = new Cart(); // 새로운 장바구니 객체 생성
        cart.setMember(member); // 장바구니에 회원 정보 설정
        return cart; // 생성된 장바구니 객체 반환
    }
}
