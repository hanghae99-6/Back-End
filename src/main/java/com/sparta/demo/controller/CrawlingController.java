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

    @GetMapping("/api/crawling/naverNews")
    public ResponseEntity<CrawlingDto> getNews() throws IOException {
        return crawlingService.getNaverNews();
    }

    @GetMapping("/api/crawling/debateColumn")
    public ResponseEntity<CrawlingDto> getColumn() throws IOException {
        return crawlingService.getColumn();
    }

    @GetMapping("/api/crawling/cheilMagazine")
    public ResponseEntity<CrawlingDto> getMagazine() throws IOException {
        return crawlingService.getMagazine();
    }
}
