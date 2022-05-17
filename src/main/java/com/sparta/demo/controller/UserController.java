package com.sparta.demo.controller;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.dto.user.MyReplyDto;
import com.sparta.demo.dto.user.UserRequestDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public void helloWorld() {
        log.info("접속 : {}", "로그");
    }

    // 닉네임 변경
    @PutMapping("/nick-name")
    public ResponseEntity<KakaoUserInfoDto> updateUserInfo(@RequestBody UserRequestDto userRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           HttpServletResponse response){
        return userService.updateUserInfo(userRequestDto.getNickName(), userDetails, response);
    }

    // 프로필 - 1. 토론 내역
//    @GetMapping("/mydebate")
//    public ResponseEntity<List<MyDebateDto>> getMyDebate(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return userService.getMyDebate(userDetails);
//    }
    @GetMapping("/mydebate")
    public Page<MyDebateDto> getMyDebatePage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestParam(value = "page",required = false) Integer page) {
        log.info("page num : {}", page);
        return userService.getMyDebatePage(userDetails, page - 1);
    }

    // 프로필 - 2. 내가 쓴 댓글
    @GetMapping("/myreply")
    public ResponseEntity<List<MyReplyDto>> getMyReply(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyReply(userDetails);
    }

    // 프로필 - 3. 공통 (총 토론 개수, 댓글 개수)
//    @GetMapping("/mytotal")
}
