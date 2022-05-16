package com.sparta.demo.controller;


import com.sparta.demo.dto.main.CrawlingDto;
import com.sparta.demo.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawlingService;

    @GetMapping("/main/crawling/naverNews")
    public ResponseEntity<CrawlingDto> getNews() throws IOException {
        return crawlingService.getNaverNews();
    }

    @GetMapping("/main/crawling/debateColumn")
    public ResponseEntity<CrawlingDto> getColumn() throws IOException {
        return crawlingService.getColumn();
    }

    @GetMapping("/main/crawling/cheilMagazine")
    public ResponseEntity<CrawlingDto> getMagazine() throws IOException {
        return crawlingService.getMagazine();
    }
}
