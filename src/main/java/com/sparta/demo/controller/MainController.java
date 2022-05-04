package com.sparta.demo.controller;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {

//    private final MainResponseDto mainResponseDto;
    private final MainService mainService;

    @GetMapping("/")
    public ResponseEntity<MainResponseDto> getMain() throws IOException {
        return mainService.getMain();
    }

    @GetMapping("/category/{catName}")
    public ResponseEntity<MainResponseDto> getCatMain(@PathVariable String catName) throws IOException {
        log.info("controller catName: {}", catName);
        return mainService.getCatMain(catName);
    }

    @GetMapping("/detail/{debateId}")
    public ResponseEntity<MainDetailResponseDto> getMainDetail(@PathVariable Long debateId) throws IOException {
        log.info("controller debateId: {}", debateId);
        return mainService.getMainDetail(debateId);
    }
}
