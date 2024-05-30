package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  ItemImgRepository extends JpaRepository<ItemImg,Long> {
    // 주어진 상품 ID에 해당하는 상품 이미지를 검색하고, 검색된 결과를 ID 오름차순으로 정렬하여 반환
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    // 주어진 상품 ID와 대표 이미지 여부에 해당하는 상품 이미지를 검색하여 반환
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);
}
