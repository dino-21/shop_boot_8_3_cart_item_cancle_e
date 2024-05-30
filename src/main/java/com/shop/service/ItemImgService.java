package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    /*
   @value 어노테이션을 통해 application.properties 파일에 등록한  itemImgLocation 값을 불러와서
   * itemImgLocation 변수에 넣어 준다.
   */
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;


    /**
     * 상품 이미지 정보를 저장하는 메서드.
     *
     * @param itemImg 저장할 상품 이미지 정보를 담고 있는 ItemImg 엔티티 객체
     * @param itemImgFile 클라이언트로부터 업로드된 이미지 파일을 나타내는 MultipartFile 객체
     * @throws Exception 파일 업로드 또는 데이터베이스 저장 중 예외가 발생할 수 있음
     */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();  // 업로드된 파일의 원래 이름
        String imgName = ""; // 서버에 저장될 파일의 이름
        String imgUrl = ""; // 웹에서 접근할 수 있는 이미지의 URL

        // 파일 업로드
        /* 사용자가 상품 이미지를 등록했다면
         * 저장한 경로와 파일의 이름, 바이트 배열을 파일 업로드 파라미터로 uploadFile 메서드를 호출한다.
         */
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes());
            // 호출 결과 로컬에 저장된 파일의 이름을 imgName 변수에 저장한다.
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 업데이트 및 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);  // 원본 파일 이름, 저장된 파일 이름, URL로 업데이트
        itemImgRepository.save(itemImg); //DB에 저장
    }


    //상품 이미지 업데이트
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        //상품 이미지를 수정한 경우 상품 이미지를 업데이트한다.
        if(!itemImgFile.isEmpty()){
            //상품 이미지 아이디를 이용하여 기존에 저장했던 상품 이미지 엔티티를 조회한다.
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존에 등록한 상품 이미지 파일이 있을 경우 해당 파일을 삭제한다.
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+
                        savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            // 업데이트한 상품 이미지 파일을 업로드한다.
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;

            /*변경된 상품 이미지 정보를 세팅한다.
             * 여기서 중요한 점은 상품 등록 때처럼 itemImgRepository.save()로직을 하지 않는다.
             * savedItemImg 엔티티는 현재 영속 상태이므로 데이터를 변경하는 것만으로 변경감지 기능이 동작하여
             * 트랜잭션이 끝날때 update 쿼리가 실행된다.
             * 여기서 중요한 것은 엔티티가 영속 상태여야 한다.
             * */
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
