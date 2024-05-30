package com.shop.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;

import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;

import com.shop.entity.QItemImg;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
//1 ItemRepositoryCustom 상속받는다.
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    //2 동적으로 쿼리를 생성하기 위해  JPAQueryFactory 클래스를 사용한다.
    private JPAQueryFactory queryFactory;

    //3 JPAQueryFactory의 생성자로 EntityManager객체를 넣어준다.
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    //4 상품 판매 상태 조건이 전체 null일 경우 null을 리턴한다.
    //결과값이 null이면 where절에서 해당 조건은 무시된다.
    // 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품판 조회된다.
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        //5 searchDateType의 값에 따라서 dateTime 의 값을 해당시간이후로 등록된 상품만 조회한다.
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //5 searchDateType 값이 1m인 경우 dateTime의 시간을 한달 전으로 세팅 후 최근 한달 동안 등록된 상품만 조회하도록 조건값을 반환한다.
    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    //6 searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에
    // 검색어를 포함하고있는 상품을 조회하도록 조건값을 반환한다.
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    /*
       1. itemSearchDto : 검색조건
       2. pageable : 페이지정보(페이지 번호, 페이지크기,정렬정보)
       3. regDtsAfter(itemSearchDto.getSearchDateType()) : 등록 날짜 필터링 조건
       4. searchSellStatusEq(itemSearchDto.getSearchSellStatus()) : 판매 상태 필터링 조건
       5. searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
            : 검색어에 따른 필터링 조건
       6. orderBy(QItem.item.id.desc()): 아이템 ID를 기준으로 내림차순 정렬
       7. offset(pageable.getOffset()): 조회할 데이터의 시작 위치를 설정
       8. limit(pageable.getPageSize()): 한 번에 조회할 데이터의 최대 개수를 설정
       9. fetch(): 최종적으로 쿼리를 실행하여 결과 리스트
       10. queryFactory.select(Wildcard.count).from(QItem.item): 전체 아이템 수를 세기 위해 count
       11. fetchOne(): 단일 결과를 반환, 여기서는 총 아이템 수
       12. new PageImpl<>(content, pageable, total): PageImpl 객체를 생성
          -> content: 조회된 아이템 리스트
          -> pageable: 페이징 정보
          -> total: 전체 아이템 수
     */
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        //7 queryFactory를 이용해서 쿼리를 생성한다.
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne() ;
        //8 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환한다.
        return new PageImpl<>(content, pageable, total);
    }

    // 검색어가 포함된 상품명을 조건으로 반환
    //1 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환한다.
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    /*
        1. queryFactory.select(...): QueryDSL을 사용하여 QMainItemDto 객체를 생성
        2. join(itemImg.item, item): itemImg와 item을 조인
        3. where(itemImg.repimgYn.eq("Y")): 대표 이미지 조건을 추가
        4. where(itemNmLike(itemSearchDto.getSearchQuery())): 아이템 이름 검색 조건을 추가
        5. orderBy(item.id.desc()): 아이템 ID를 기준으로 내림차순 정렬
        6. offset(pageable.getOffset()): 조회할 데이터의 시작 위치를 설정
        7. limit(pageable.getPageSize()): 한 번에 조회할 데이터의 최대 개수를 설정
        8. fetch(): 최종적으로 쿼리를 실행하여 결과 리스트
        9. List<MainItemDto> content
            -> new QMainItemDto()생성된 객체를 MainItemDto로 변환해서
            -> content가 리스트로 모든 정보를 가지고있음.
     */
    @Override
    // 검색 조건에 맞는 메인 아이템 페이지를 반환
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // 검색 조건에 맞는 메인 아이템 리스트를 쿼리하여 가져옴
        List<MainItemDto> content = queryFactory
                .select(
                        //2 @MainItmeDto의 생성자에 반환할 값들을 넣어준다.
                        //@QueryProjection을 사용하면 DTO로 바로 조회가 가능하다.
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)//3 itmeImg와 item을 내부 조인한다.
                .where(itemImg.repimgYn.eq("Y"))  //4 상품 이미지의 경우 대표 상품 이미지만 불러온다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 검색 조건에 맞는 총 아이템 수를 쿼리하여 가져옴
        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;
        // PageImpl 객체로 반환하여 페이징 처리된 결과를 반환
        return new PageImpl<>(content, pageable, total);
    }

}