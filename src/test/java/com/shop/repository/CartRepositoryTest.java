package com.shop.repository;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Cart;
import com.shop.entity.Member;
import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Log4j2
class CartRepositoryTest {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    //회원 엔티티 생성
    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(passwordEncoder.encode("1234"));
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    @Commit
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);  //0x10  test@email.com

        Cart cart = new Cart();

        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); //영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝나면 DB에 반영
        em.clear();  // 영속성 컨텍스트 엔티티 조회 후  엔티티가 없으면 DB조회
        // DB에서 장바구니 엔티티를 가져올때 회원 엔티티도 같이 가져오는지 보기 위해서 영속성컨텍스트비움

        //저장된 장바구니 엔티티 조회
        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);
        //처음에 저장한 member 엔티티의 id와 savedCart에 매핑된 member 엔티티의 id를 비교
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}