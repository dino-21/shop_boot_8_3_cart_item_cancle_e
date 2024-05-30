package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {
    private Long id; // 이미지 ID

    private String imgName; // 이미지 이름

    private String oriImgName;  // 원본 이미지 이름

    private String imgUrl;  // 이미지 URL

    private String repImgYn;  // 대표 이미지 여부

    //멤버변수로 ModelMapper객체를 추가, ModelMapper 인스턴스 생성
    private static ModelMapper modelMapper = new ModelMapper();

    // static 메서드로 주어진 ItemImgDto 객체를 복사하여 새로운 ItemImgDto 객체를 반환
    // ItemImg 엔티티 객체를 파라미터로 받아서 ItemImgDto로 반환한다.
    // static 메서드로 선언해 ItemImgDto 객체를 생성하지 않아도 호출할 수 있다.
    public static ItemImgDto of(ItemImg itemImg) {
        // ModelMapper를 사용하여 객체 매핑
        return modelMapper.map(itemImg,ItemImgDto.class);
    }
}
