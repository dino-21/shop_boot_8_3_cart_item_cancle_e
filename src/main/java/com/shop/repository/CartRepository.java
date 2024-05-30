package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart, Long> {
    // 회원 ID로 장바구니를 찾는 메서드
    Cart findByMemberId(Long memberId);
}

