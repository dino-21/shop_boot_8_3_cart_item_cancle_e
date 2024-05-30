package com.shop.controller;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;


    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){

        // 페이지 정보를 설정: 요청된 페이지가 있으면 해당 페이지를 사용하고,
        // 없으면 첫 번째 페이지를 사용 (한 페이지에 6개의 항목)
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);

        // 검색 조건과 페이징 정보를 기반으로 메인 아이템 페이지를 가져옴
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        // 모델에 아이템 목록, 검색 조건 및 최대 페이지 수를 추가
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        // main 뷰를 반환
        return "main";
    }
}
