package com.sparta.demo.controller;


import com.sparta.demo.dto.main.CrawlingDto;
import com.sparta.demo.service.CrawlingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Api(tags = {"Crawling"})
@Controller
@RequiredArgsConstructor
@RequestMapping("/main/crawling")
public class CrawlingController {
    private final CrawlingService crawlingService;

    @ApiOperation(value = "네이버뉴스 기사 크롤링", notes = "네이버 기사 크롤링- 날짜, 페이지에 따라")
    @GetMapping("/naverNews")
    public ResponseEntity<CrawlingDto> getNews() throws IOException {
        return crawlingService.getNaverNews();
    }

    @ApiOperation(value = "한국디베이트신문 칼럼 크롤링", notes = "한국디베이트신문 칼럼 크롤링- 날짜에 따라")
    @GetMapping("/debateColumn")
    public ResponseEntity<CrawlingDto> getColumn() throws IOException {
        return crawlingService.getColumn();
    }

    @ApiOperation(value = "제일 기획 메거진 기사 크롤링 ", notes = "제일 기획 메거진 기사 크롤링- 날짜에 따라")
    @GetMapping("/cheilMagazine")
    public ResponseEntity<CrawlingDto> getMagazine() throws IOException {
        return crawlingService.getMagazine();
    }
}
