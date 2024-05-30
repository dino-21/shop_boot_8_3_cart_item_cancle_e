package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일 정보를 이용하여 회원을 찾는 메서드
    Member findByEmail(String email);
}
