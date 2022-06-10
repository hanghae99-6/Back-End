package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.DebateInfoDto;
import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.dto.user.MyReplyDto;
import com.sparta.demo.dto.user.UserRequestDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.UserService;
import com.sparta.demo.validator.ErrorResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "댓글 관리 API", tags = {"Reply"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class UserController {

    private final UserService userService;

    // 닉네임 변경
    @ApiOperation(value = "유저 닉네임 수정", notes = "유저 닉네임 수정")
    @PutMapping("/nick-name")
    public ResponseEntity<KakaoUserInfoDto> updateUserInfo(@RequestBody UserRequestDto userRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           HttpServletResponse response){
        return userService.updateUserInfo(userRequestDto.getNickName(), userDetails, response);
    }

    // 프로필 - 1. 토론 내역
    @ApiOperation(value = "유저 페이지- 토론 내역 조회", notes = "유저 페이지- 토론 내역 조회")
    @GetMapping("/mydebate")
    public ResponseEntity<List<MyDebateDto>> getMyDebate(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyDebate(userDetails);
    }


    // 프로필 - 2. 내가 쓴 댓글
    @ApiOperation(value = "유저 페이지- 내가 쓴 댓글 조회", notes = "유저 페이지- 내가 쓴 댓글 조회")
    @GetMapping("/myreply")
    public ResponseEntity<List<MyReplyDto>> getMyReply(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyReply(userDetails);
    }

    // 프로필 - 3. 내가 쓴 토론 수정
    @ApiOperation(value = "유저 페이지- 토론 내역 수정", notes = "유저 페이지 -토론 내역 수정")
    @PutMapping("/mydebate/{debateId}")
    public ResponseEntity<ErrorResult> editMydebate(@PathVariable Long debateId,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody DebateInfoDto debateInfoDto){
        return userService.editMydebate(debateId, userDetails, debateInfoDto);
    }


    // 프로필 - 4. 내가 쓴 토론 삭제
    @ApiOperation(value = "유저 페이지- 토론 내역 삭제", notes = "유저 페이지 -토론 내역 삭제")
    @DeleteMapping("/mydebate/{debateId}")
    public ResponseEntity<ErrorResult> deleteMydebate(@PathVariable Long debateId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("controller debateId: {}", debateId);
        return userService.deleteMydebate(debateId, userDetails);
    }

    // 관리자용 - 강제 토론방 삭제
    @DeleteMapping("/mydebate/admin/{debateId}")
    public void deleteMydebateForAdmin(@PathVariable Long debateId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("controller debateId: {}", debateId);
        userService.deleteMydebateForAdmin(debateId, userDetails);
    }


}
