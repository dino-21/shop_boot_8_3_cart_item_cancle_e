package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;  //상품 데이터베이스 접근 객체
    private final MemberRepository memberRepository; // 회원 데이터베이스 접근 객체
    private final OrderRepository orderRepository; // 주문 데이터베이스 접근 객체


    private final ItemImgRepository itemImgRepository;  // 상품 이미지 데이터베이스 접근 객체


    //주문을 생성하는 메서드
    // 매개변수 (orderDto 주문 정보를 담고 있는 DTO 객체,  email 주문하는 회원의 이메일, 생성된 주문의 ID)
    public Long order(OrderDto orderDto, String email){
        // 1 주문할 상품을 데이터베이스에서 조회. 존재하지 않으면 예외 발생
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 2 회원 정보를 이메일로 조회
        Member member = memberRepository.findByEmail(email);

        // 주문 상품 리스트를 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        // 3 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        // 주문 상품 리스트에 추가
        orderItemList.add(orderItem);

        // 4주문 생성
        Order order = Order.createOrder(member, orderItemList);

        // 5 주문을 데이터베이스에 저장
        orderRepository.save(order);

        // 생성된 주문의 ID 반환
        return order.getId();
    }



    /*
       이메일에 해당하는 사용자의 주문 목록을 조회하고,
       각 주문에 대해 필요한 정보를 DTO로 변환하여 페이징된 형태로 반환합니다.
       이를 통해 호출자는 사용자의 주문 이력을 엑세스 한다.
    */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){

        // 1 아이디와 페이징조건을 이용하여 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 2 유저 주문 개수 조회
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        // 3 주문리스트 순회하면서 구매 이력 페이지에 전달할 DTO 생성
        for(Order order : orders){
            OrderHistDto orderHistDto = new OrderHistDto(order);

            List<OrderItem> orderItems = order.getOrderItems(); //해당 주문의 주문 항목 리스트를 가져온다.

            for(OrderItem orderItem : orderItems){
                //4 주문 항목의 대표 이미지를 조회
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                //각 주문 항목에 대해 OrderItemDto 객체를 생성
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                //생성된 OrderItemDto를 OrderHistDto에 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            //OrderHistDto를 리스트에 추가
            orderHistDtos.add(orderHistDto);
        }

        // 5 조회된 OrderHistDto 객체들의 리스트,  페이징 정보,  전체 주문 수
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }


    /*
     주문의 유효성을 검사한다.
     현재 사용자의 이메일과 주어진 주문 ID로 조회된 주문의 회원 이메일을 비교하여
     일치하는지 확인합니다. 일치하면 true를 반환하고, 그렇지 않으면 false를 반환한다.
 */
    @Transactional(readOnly = true)
    // 1 현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사, 같을 때 true를 반환
    public boolean validateOrder(Long orderId, String email){
        // 현재 사용자의 이메일을 이용하여 회원 정보를 조회
        Member curMember = memberRepository.findByEmail(email);
        // 주어진 주문 ID로 주문을 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 조회된 주문의 회원 정보를 가져옴
        Member savedMember = order.getMember();

        // 현재 사용자의 이메일과 조회된 주문의 회원 이메일을 비교하여 일치 여부를 확인
        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false; // 이메일이 일치하지 않으면 false 반환
        }

        return true; // 이메일이 일치하면 true 반환
    }


    /*
    주문을 취소하는 메서드
    주어진 주문 ID로 주문을 조회하고, 해당 주문을 취소한다.
*/
    public void cancelOrder(Long orderId){
        // 주어진 주문 ID로 주문을 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 조회된 주문을 취소
        //2 주문 취소상태로변경하면 변경 감지 기능에 의해서 트랜잭션이 끝날때 update 쿼리가 실행
        order.cancelOrder();
    }


    /*
    1. 이메일을 통해 Member 객체를 조회
    2. 각 OrderDto 객체를 순회하면서, Item 객체를 조회하고 OrderItem 객체를 생성하여 리스트에 추가
    3. OrderItem 리스트와 Member 객체를 사용하여 Order 객체를 생성
    4. 생성된 Order 객체를 DB 저장하고, 생성된 주문의 ID를 반환
  */
    //주문 DTO 목록을 사용하여 주문을 생성하는 메소드
    public Long orders(List<OrderDto> orderDtoList, String email){

        // 이메일을 통해 회원 정보 조회
        Member member = memberRepository.findByEmail(email);

        // 주문 아이템 목록 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        // 1 주문할 상품 리스트를 만든다.
        for (OrderDto orderDto : orderDtoList) {
            // 주문할 상품 정보 조회
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            // 주문 아이템 생성 및 주문 아이템 목록에 추가
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        // 2 주문 생성, 현재 로그인한 회원과 주문 상품 목록을 이용하여 주문 엔티티를 만든다.
        Order order = Order.createOrder(member, orderItemList);

        // 3 주문 저장
        orderRepository.save(order);
        // 생성된 주문의 ID 반환
        return order.getId();
    }
}
