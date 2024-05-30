package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> , QuerydslPredicateExecutor<Item>, ItemRepositoryCustom{
    // 아이템의 이름으로 검색하는 메소드
    List<Item> findByItemNm(String itemNm);


    //가격이 특정 값보다 큰 아이템을 조회하는 메소드
    List<Item> findByPriceGreaterThan(int price);


    // 상품명이나 상품 상세 설명 중 하나라도 일치하는 아이템을 조회하는 메소드
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    // 가격이 특정 값보다 작은 아이템을 조회하는 메소드
    List<Item> findByPriceLessThan(Integer price);


    // 특정 가격보다 작은 아이템을 조회하되, 조회 결과를 가격에 대해 내림차순으로 정렬하는 메소드
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);


    //Item 엔티티(클래스) 조회
    // 아이템 상세 설명에 일부가 포함된 경우 해당하는 아이템을 내림차순으로 가격에 따라 조회하는 메소드
    @Query("select i from  Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //위의 코드는 mysql에서 조회(테이블로 조회)와 같음
    //select * from item  where item_detail like '%설명%' order by price desc;
    //select * from item  where item_detail like '%설명2%';



    // 네이티브 쿼리를 사용하여 아이템 상세 설명에 일부가 포함된 경우,
    // 해당하는 아이템을 내림차순으로 가격에 따라 조회하는 메소드
    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);


}
