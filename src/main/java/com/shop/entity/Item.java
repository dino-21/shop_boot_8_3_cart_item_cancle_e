package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;       //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    //  private LocalDateTime regTime;  //등록시간

    // private LocalDateTime updateTime;  //수정시간

    //  Item 엔티티의 필드 값을 ItemFormDto 객체의 값으로 업데이트
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();    // DTO에서 가져온 상품명을 엔티티의 상품명으로 설정
        this.price = itemFormDto.getPrice();  // DTO에서 가져온 가격을 엔티티의 가격으로 설정
        this.stockNumber = itemFormDto.getStockNumber();  // DTO에서 가져온 재고 수량을 엔티티의 재고 수량으로 설정
        this.itemDetail = itemFormDto.getItemDetail();// DTO에서 가져온 상품 상세 설명을 엔티티의 상세 설명으로 설정
        this.itemSellStatus = itemFormDto.getItemSellStatus(); // DTO에서 가져온 상품 판매 상태를 엔티티의 판매 상태로 설정
    }

    //상품의 재고 수량을 지정된 수량만큼 감소시킨다.
    //stockNumber 감소시킬 재고 수량
    // OutOfStockException 재고가 부족할 경우 발생하는 예외
    public void removeStock(int stockNumber){
        // 1 재고 감소 후 남은 재고 수량 계산
        int restStock = this.stockNumber - stockNumber;

        // 2 남은 재고 수량이 0보다 작으면 재고 부족 예외 발생
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }

        // 3 현재 재고 수량을 남은 재고 수량으로 업데이트
        this.stockNumber = restStock;
    }

    //상품의 재고를 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}