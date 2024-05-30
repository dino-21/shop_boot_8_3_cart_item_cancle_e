package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping ("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    private final PasswordEncoder passwordEncoder;

    @GetMapping(value="/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }


//    @PostMapping(value="/new")
//    public String memberFormSubmit(MemberFormDto memberFormDto) {
//        Member member = Member.createMember(memberFormDto, passwordEncoder);
//        memberService.saveMember(member);
//        return "redirect:/members/";
//    }


    @PostMapping(value="/new")
    public String memberFormSubmit(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        // 유효성 검사에서 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            // 오류가 있을 경우 회원 가입 폼 페이지로 이동
            return "member/memberForm";
        }

        try {
            // MemberFormDto를 사용하여 Member 객체를 생성하고 비밀번호를 암호화
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            // 생성된 Member 객체를 저장
            memberService.saveMember(member);
        } catch(IllegalStateException e){
            // 예외가 발생할 경우, 예외 메시지를 모델에 추가하고 회원 가입 폼 페이지로 이동
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        // 회원 정보가 성공적으로 저장되면 회원 목록 페이지로 리다이렉트
        return "redirect:/members/";
    }


    @GetMapping(value="/")
    public String main(){
        return "main";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginMemberError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }
}
