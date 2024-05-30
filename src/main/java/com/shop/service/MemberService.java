package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일을 사용하여 회원 정보를 조회
        Member member  = memberRepository.findByEmail(email);

        // 조회된 회원 정보가 없으면 UsernameNotFoundException 예외를 발생
        if(member == null) {
            throw new UsernameNotFoundException(email);
        }

        // 조회된 회원 정보를 사용하여 UserDetails 객체를 생성하여 반환
        return User.builder()
                .username(member.getEmail()) // 회원의 이메일을 사용자명으로 설정
                .password(member.getPassword())  // 회원의 비밀번호를 설정
                .roles(member.getRole().toString()) // 회원의 역할(role)을 설정
                .build();
    }
}
