package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Item 엔티티를 위한 커스텀 리포지토리 인터페이스
 * 이 인터페이스는 Spring Data JPA가 기본적으로 제공하지 않는 사용자 정의 쿼리 메서드를 선언한다.
 */
//메서드는 관리자 페이지에서 검색 조건에 따라 아이템 목록을 페이징 처리하여 반환하기 위해 사용
public interface ItemRepositoryCustom {

    /**
     * 관리자 페이지에서 검색 조건에 따라 아이템의 페이징된 목록을 가져온다.
     *
     * @param itemSearchDto 아이템 검색 조건을 담고 있는 데이터 전송 객체
     * @param pageable      페이징 정보
     * @return 검색 조건에 맞는 아이템의 페이지를 반환
     */
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);


    /**
     * 관리자 페이지에서 검색 조건에 따라 아이템의 페이징된 목록을 가져온다.
     * 반환되는 아이템은 MainItemDto로 매핑된다.
     *
     * @param itemSearchDto 아이템 검색 조건을 담고 있는 데이터 전송 객체
     * @param pageable      페이징 정보
     * @return 검색 조건에 맞는 아이템의 페이지를 반환
     */
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
