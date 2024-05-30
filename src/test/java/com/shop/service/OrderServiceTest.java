package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Log4j2
public class OrderServiceTest {

    @Autowired
    private OrderService orderService; // 주문 서비스

    @Autowired
    private OrderRepository orderRepository; // 주문 레포지토리

    @Autowired
    private ItemRepository itemRepository; // 상품 레포지토리

    @Autowired
    MemberRepository memberRepository;  // 회원 레포지토리

    // 1  상품 정보를 저장하고 반환
    // 리턴 저장된 상품
    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("졸음껌");
        item.setPrice(10000);
        item.setItemDetail("졸음껌 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    // 2 회원 정보를 저장하고 반환
    // 리턴 저장된 회원
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);

    }

    @Test
    @DisplayName("주문 테스트")
    @Commit // 테스트 후 트랜잭션이 커밋되어 데이터베이스에 반영
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);  // 3 주문할 상품과 상품수량을 orderDto 객체에 세팅
        orderDto.setItemId(item.getId());  // 4  주문할 상품과 상품수량을 orderDto 객체에 세팅
        // 5 주문 생성, 주문 로직 호출 결과 생성된 주문 번호를 orderId 변수에 저장
        Long orderId = orderService.order(orderDto, member.getEmail());
        // 6 생성된 주문을 데이터베이스에서 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 주문 상품 리스트 가져오기
        List<OrderItem> orderItems = order.getOrderItems();
        // 7  총 주문 가격 계산
        int totalPrice = orderDto.getCount()*item.getPrice();
        // 8  주문한 상품 가격과 DB에  저장된 상품의 가격을 비교하여 주문의 총 가격이 일치하는지 검증
        assertEquals(totalPrice, order.getTotalPrice());
    }

}
