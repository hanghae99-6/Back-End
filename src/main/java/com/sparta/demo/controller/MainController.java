package com.sparta.demo.controller;

import com.sparta.demo.dto.main.DebateVoteRequestDto;
import com.sparta.demo.dto.main.DebateVoteResponseDto;
import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyLikesResponseDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.DebateVote;
import com.sparta.demo.service.DebateVoteService;
import com.sparta.demo.service.LikesService;
import com.sparta.demo.service.MainService;
import com.sparta.demo.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {

//    private final MainResponseDto mainResponseDto;
    private final MainService mainService;
    private final ReplyService replyService;
    private final LikesService likesService;
    private final DebateVoteService debateVoteService;

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






    // TODO: 메인의 댓글 달기 기능
    //  api 설계 기준으로 /main으로 시작하는 api주소여서 mainController에 넣었으나 ReplyController로 다시 뺄지 생각해봐야함
    @PostMapping("/{debateId}/reply")
    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId, @RequestBody Map<String, String> param) {
        // TODO: user과 합치면 userdetails에서 유저정보 받아와야함
//    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId, @RequestParam String reply, @AuthenticationPrincipal UserDetailsImpl userDetails){
//        return replyService.writeReply(debateId, userDetails);
        log.info("controller debateId: {}", debateId);
        log.info("controller reply : {}", param.get("reply"));
        return replyService.writeReply(debateId, param.get("reply"));
    }



    // TODO: 메인의 댓글의 좋아요 달기 기능
    //  api 설계 기준으로 /main으로 시작하는 api주소여서 mainController에 넣었으나 LikesController로 다시 뺄지 생각해봐야함
    @PostMapping("/reply/likes")
    public ResponseEntity<ReplyLikesResponseDto> getLikes(@RequestBody ReplyLikesRequestDto replyLikesRequestDto, HttpServletRequest request){
        return likesService.getLikes(replyLikesRequestDto,request);
    }

    // TODO: 메인상세페이지에서의 찬반 기능
    //  api 설계 기준으로 /main으로 시작하는 api주소여서 mainController에 넣었으나 DebateVoteController로 다시 뺄지 생각해봐야함
    @PostMapping("/debate/vote")
    public ResponseEntity<DebateVoteResponseDto> getVote(@RequestBody DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request){
        log.info("debateId {}: ",debateVoteRequestDto.getDebateId());
        log.info("side {}: ",debateVoteRequestDto.getSide());
        return debateVoteService.getVote(debateVoteRequestDto, request);
    }


}
