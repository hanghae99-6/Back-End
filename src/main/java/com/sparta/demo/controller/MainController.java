package com.sparta.demo.controller;

import com.sparta.demo.dto.main.*;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import com.sparta.demo.repository.OneClickRepository;
import com.sparta.demo.repository.OneClickUserRepository;
import com.sparta.demo.service.MainService;
import com.sparta.demo.util.GetIp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
@Api(value = "mainPage 관리 API", tags = {"Main"})
public class MainController {

    private final MainService mainService;
    private final OneClickRepository oneClickRepository;
    private final OneClickUserRepository oneClickUserRepository;

    @ApiOperation(value = "메인페이지 핫피치 보여주기", notes = "메인페이지 핫피치 랜덤 6개 보여주기")
    @GetMapping("/")
    public ResponseEntity<List<MainCategoryResDto>> getMainAll(){
        return mainService.getMainAll();
    }

    @ApiOperation(value = "카테고리별 토론 내역 보기", notes = "카테고리별 토론 내역 보기")
    @GetMapping("/category/{catName}")
    public ResponseEntity<List<MainCategoryResDto>> getCategoryMain(@PathVariable String catName){
        log.info("controller catName: {}", catName);
        return mainService.getCategoryMain(catName);
    }

    @ApiOperation(value = "핫피치 상세보기", notes = "<strong>상세보기</strong> debateId를 통해서 확인가능")
    @GetMapping("/detail/{debateId}")
    public ResponseEntity<MainDetailResponseDto> getMainDetail(@PathVariable Long debateId, HttpServletRequest request) {
        log.info("controller debateId: {}", debateId);
        return mainService.getMainDetail(debateId, request);
    }

    @ApiOperation(value = "원클릭 찬반 토론 보기")
    @GetMapping("/one-click")
    public ResponseEntity<List<OneClickResponseDto>> getOneClick(HttpServletRequest request) {
        return mainService.getOneClick(request);
    }

    @ApiOperation(value = "원클릭 찬반 선택하기")
    @PutMapping("/one-click")
    public ResponseEntity<List<OneClickResponseDto>> createOneClickVote(@RequestBody OneClickRequestDto oneClickRequestDto,
                                                                 HttpServletRequest request) {
        String userIp = GetIp.getIp(request);

        SideTypeEnum sideTypeEnum = SideTypeEnum.typeOf(oneClickRequestDto.getSide()); // enum 값으로 변형

        OneClick oneClick = oneClickRepository.findById(oneClickRequestDto.getOneClickId()).orElseThrow( // oneClickTopic 으로 OneClick 객체를 찾아옴
                () -> new IllegalStateException("없는 토픽입니다.")
        );

        Optional<OneClickUser> oneClickUser = oneClickUserRepository.findByUserIpAndOneClickId(userIp, oneClickRequestDto.getOneClickId());

        log.info("sideTypeEnum.getTypeNum(): {}", sideTypeEnum.getTypeNum());

        if(oneClickUser.isPresent()){
            return mainService.updateOneClickVote(sideTypeEnum,oneClick,oneClickUser.get(), request);
        }
        return mainService.createOneClickVote(oneClickRequestDto, userIp, sideTypeEnum, oneClick, request);
    }

}
