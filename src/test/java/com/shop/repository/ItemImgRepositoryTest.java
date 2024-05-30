package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.Role;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.entity.Member;
import com.shop.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemImgRepositoryTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    MemberRepository memberRepository;

    // 파일을 생성하여 리스트로 반환하는 메서드
    List<MultipartFile> createMultipartFiles() throws Exception {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        // 5개의 가짜 이미지 파일 생성
        for (int i = 0; i < 5; i++) {
            String path = "C:/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @Commit
    void saveItem() throws Exception {
        // 사용자 생성
        Member newMember = new Member();
        newMember.setName("강산이");
        newMember.setEmail("kangsan@email.com");// 이메일
        newMember.setPassword("1234"); // 패스워드
        newMember.setRole(Role.ADMIN);// 관리자 권한
        memberRepository.save(newMember);

        memberRepository.save(newMember);
        em.flush();
        em.clear();

        // 상품 등록 테스트에 사용할 상품 정보 설정
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);

        // 사용자 정보를 설정하여 상품 등록 테스트 실행
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(newMember.getEmail(), newMember.getPassword())
        );

        // 파일 생성 및 상품 등록
        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // Item item = itemRepository.findById(itemId)
        //       .orElseThrow(() -> new EntityNotFoundException());

        // 테스트 검증
        assertEquals(itemFormDto.getItemNm(), item.getItemNm()); // 상품명 검증
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus()); // 상품 판매 상태 검증
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail()); // 상품 상세 정보 검증
        assertEquals(itemFormDto.getPrice(), item.getPrice());  // 상품 가격 검증
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber()); // 상품 재고 수 검증
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName()); // 상품 이미지 파일명 검증
    }
}