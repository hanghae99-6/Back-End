package com.sparta.demo.controller;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.dto.main.OneClickResponseDto;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.service.MainService;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
@Api(value = "mainPage 관리 API", tags = {"Main"})
public class MainController {

    private final MainService mainService;

    @ApiOperation(value = "메인페이지 핫피치 보여주기", notes = "메인페이지 핫피치 랜덤 6개 보여주기")
    @GetMapping("/")
    public ResponseEntity<MainResponseDto> getMain(){
        return mainService.getMain();
    }

    @ApiOperation(value = "카테고리별 토론 내역 보기", notes = "카테고리별 토론 내역 보기")
    @GetMapping("/category/{catName}")
    public ResponseEntity<MainResponseDto> getCatMain(@PathVariable String catName){
        log.info("controller catName: {}", catName);
        return mainService.getCatMain(catName);
    }

    @ApiOperation(value = "핫피치 상세보기", notes = "<strong>상세보기</strong> debateId를 통해서 확인가능")
    @GetMapping("/detail/{debateId}")
    public ResponseEntity<MainDetailResponseDto> getMainDetail(@PathVariable Long debateId, HttpServletRequest request) {
        log.info("controller debateId: {}", debateId);
        return mainService.getMainDetail(debateId, request);
    }

    @ApiOperation(value = "원클릭 찬반 토론 가져오기")
    @GetMapping("/one-click")
    public ResponseEntity<List<OneClickResponseDto>> getOneClick(HttpServletRequest request) {
        return mainService.getOneClick(request);
    }

    @ApiOperation(value = "원클릭 찬반 선택하기")
    @PutMapping("/one-click")
    public ResponseEntity<List<OneClickResponseDto>> sumOneClick(@RequestBody OneClickRequestDto oneClickRequestDto,
                                                HttpServletRequest request) {
        return mainService.sumOneClick(oneClickRequestDto, request);
    }
}
