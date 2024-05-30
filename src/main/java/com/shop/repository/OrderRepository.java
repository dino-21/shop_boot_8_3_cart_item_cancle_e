package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository <Order, Long>{
    //1 특정 회원의 이메일을 기준으로 주문 목록을 조회하는 쿼리
    // 이메일에 해당하는 주문들을 주문 날짜 기준으로 내림차순 정렬하여 페이징 처리
    @Query("select o from Order o " +  "where o.member.email = :email " +  "order by o.orderDate desc" )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    //특정 회원의 이메일을 기준으로 주문의 개수를 조회하는 쿼리
    @Query( "select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email); //2 현재 로그인한 회원의 주문 갯수가 몇개인지 조회


}
