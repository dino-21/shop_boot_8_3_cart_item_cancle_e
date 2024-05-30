package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;  // 주문 서비스


    /**
     * 주문 생성 요청을 처리하는 메서드
     *
     * @param orderDto 주문 정보를 담고 있는 DTO 객체
     * @param bindingResult 유효성 검사 결과를 담고 있는 객체
     * @param principal 인증된 사용자의 정보를 담고 있는 객체
     * @return ResponseEntity 주문 생성 결과를 담은 응답 객체
     */
    @PostMapping(value = "/order")
    //1 스프링에서 비동기처리할때 @RequestBody 와 @ResponseBody 어노테이션 사용
    public @ResponseBody ResponseEntity order(
            @RequestBody @Valid OrderDto orderDto,
            BindingResult bindingResult, Principal principal ){

        // 2 유효성 검사에서 오류가 발생한 경우, 주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            // 3 에러 정보를 ResponseEntity 객체에 담아서 반환한다.
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 4 인증된 사용자의 이메일을 가져옴, principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회한다.
        String email = principal.getName();

        Long orderId;

        try{ // 5 주문 생성, 화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문 로직을 호출한다.
            orderId = orderService.order(orderDto,email);
        }catch (Exception e){ // 주문 생성 중 오류 발생 시
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 6 주문 생성 성공 시 주문 ID 반환, 결과값으로 주문번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환한다.
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }


    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){
        // 1 페이지 번호와 페이지 사이즈를 설정하여 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        // 2 주문 이력을 페이지별로 조회
        Page<OrderHistDto> OrderHistDtoList =
                orderService.getOrderList(principal.getName(), pageable);

        // 모델에 조회된 주문 이력과 페이지 정보 추가
        model.addAttribute("orders", OrderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist"; // 주문 이력 페이지로 이동
    }


    /**
     * 주문 취소 요청을 처리하는 메서드
     *
     * @param orderId 취소할 주문의 ID
     * @param principal 인증된 사용자의 정보를 담고 있는 객체
     * @return ResponseEntity 취소 결과를 담은 응답 객체
     */
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){
        // 1 주문 취소 권한이 없는 경우
        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId); // 2 주문 취소
        return new ResponseEntity<Long>(orderId, HttpStatus.OK); // 취소 결과 반환
    }
}
