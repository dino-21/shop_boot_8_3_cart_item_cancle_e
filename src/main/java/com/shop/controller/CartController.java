package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final CartService cartService;


    // 장바구니에 상품 추가하는 POST 요청 처리 메서드
    @PostMapping(value = "/cart")
    //매개변수 (클라이언트가 보낸 장바구니 항목 데이터, 유효성 검사 결과, 현재 로그인한 사용자 정보)
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal){

        // 1 유효성 검사 결과 오류가 있으면 오류 응답 반환
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 2 현재 로그인한 사용자의 이메일 가져오기
        String email = principal.getName();

        Long cartItemId;

        try{
            // 3 장바구니에 상품 추가하고, 장바구니 항목의 ID 반환
            cartItemId = cartService.addCart(cartItemDto, email);
        }catch (Exception e){
            // 예외 발생 시 예외 메시지와 함께 오류 응답 반환
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 4 성공적으로 처리된 경우 장바구니 항목의 ID 반환
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public String orderHist(Principal principal, Model model){
        // 1현재 로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품 정보를 조회한다.
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());

        // 2 조회한 장바구니 상품 정보를 뷰로 전달한다.
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList"; // 장바구니 목록 페이지로 이동
    }




    //1 PATCH 요청은 해당 ID를 가진 장바구니 아이템을 수정하는 데 사용
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        // 2 수량이 0 이하인 경우 에러 메시지와 함께 BAD_REQUEST 상태 반환
        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
            // 사용자 권한 검증 실패 시 에러 메시지와 함께 FORBIDDEN 상태 반환
        }//3 수정권한 체크
        else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 4 장바구니 상품 개수 업데이트
        cartService.updateCartItemCount(cartItemId, count);

        // 업데이트된 항목의 ID와 함께 OK 상태 반환
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }


    // 1 장바구니 항목 삭제
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){
        // 2 사용자 권한 검증 실패 시 에러 메시지와 함께 FORBIDDEN 상태 반환
        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 3 장바구니 항목 삭제
        cartService.deleteCartItem(cartItemId);

        // 삭제된 항목의 ID와 함께 OK 상태 반환
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    // 장바구니에 담긴 상품을 주문하는 엔드포인트, 장바구니에 담긴 상품을 주문하는 RESTful API 엔드포인트
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){
        // 주문할 상품 목록 가져오기
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        // 1 주문할 상품이 없는 경우
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        // 2각 상품에 대한 주문 권한 확인
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        // 3주문 처리 및 주문 ID 반환, 주문 로직 호출결과 생성된 주문 번호를 반환 받는다.
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());

        // 4 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
