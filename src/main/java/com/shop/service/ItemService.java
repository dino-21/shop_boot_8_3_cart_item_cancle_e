package com.shop.service;


import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    //상품 정보를 데이터베이스에 저장하고 조회하는 데 사용
    private final ItemRepository itemRepository;

    //이미지 파일을 처리하고 저장하는 데 사용
    private final ItemImgService itemImgService;

    //상품 이미지 정보를 데이터베이스에 저장하고 조회하는 데 사용
    private final ItemImgRepository itemImgRepository;

    /**
     * itemFormDto: 상품 등록 폼에서 입력받은 데이터를 담고 있는 DTO(Data Transfer Object).
     * itemImgFileList: 클라이언트로부터 업로드된 이미지 파일들의 리스트.
     * throws Exception: 파일 업로드 및 데이터베이스 저장 과정에서 발생할 수 있는 예외를 호출자에게 전달
     */

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 등록
        // 상품 등록폼으로부터 입력받은 데이터를 이용하여 item객체를 생성
        Item item = itemFormDto.createItem();
        itemRepository.save(item);// 상품데이터를 저장

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            //첫번째 이미지일 경우 대표이미지 여부 Y로 세팅 나머지 상품은 N로 설정
            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            // 상품이미지 정보 저장(각 이미지 파일을 저장하고 관련 정보를 데이터베이스에 저장)
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        //저장된 상품의 ID를 반환
        return item.getId();
    }




    /**
     * 상품 상세 정보를 조회하는 메서드
     *
     * @param itemId 조회할 상품의 ID
     * @return ItemFormDto 조회된 상품 정보를 담은 DTO
     */
    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션을 설정
    public ItemFormDto getItemDtl(Long itemId){
        // 이미지조회, 등록순으로 가지고 오기 위해서 상품 이미지 아이디 오름차순으로 가지고 옴
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 조회된 상품 이미지 엔티티(ItemImg)를 DTO(ItemImgDto)객체로 변환하여 리스트에 추가
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        // 상품 ID를 기준으로 상품 엔티티 조회하고, 존재하지 않으면 예외를 던짐
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 조회된 상품 엔티티를 DTO로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        // 상품 이미지 DTO 리스트를 설정
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        // 상품 상세 정보를 담은 DTO를 반환
        return itemFormDto;
    }




    //ItemFormDto와 이미지 파일 목록을 사용하여 기존 상품과 관련된 정보를 업데이트하는 기능을 수행
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        //상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회한다.
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        //상품 등록 화면으로부터 전달받은 ItemFormDto를 통해 상품 엔티티를 업데이트한다.
        item.updateItem(itemFormDto);

        //상품 이미지 아이디 리스트를 조회한다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        //상품 이미지를 업데이트하기 위해서 updateItemImg()메서드에 상품 이미지 아이디와 , 상품 이미지 파일 정보를 파라미터로 전달
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }


    // 읽기 전용 트랜잭션으로 설정
    @Transactional(readOnly = true)
    // 검색 조건과 페이징 정보를 기반으로 아이템 페이지 반환
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    // 읽기 전용 트랜잭션으로 설정
    @Transactional(readOnly = true)
    //메인 페이지에서 검색 조건에 따라 메인 상품의 페이징된 목록을 가져온다.
    //itemSearchDto 아이템 검색 조건을 담고 있는 데이터 전송 객체,  페이징 정보,  검색 조건에 맞는 메인 상품의 페이지를 반환
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
