package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.dto.debate.DebateRoomResponseDto;
import com.sparta.demo.service.DebateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// 그냥 컨트롤러 쓰는 건 약간 영문을 모르겠네요?
@Controller
@RequiredArgsConstructor
// url에서 그냥 api는 제외하고 /debate만 둘까봐요 그게 더 깔끔할지두?
@RequestMapping("/api/debate")
public class DebateController {
    private final DebateService debateService;

    @PostMapping("/link")
    public DebateLinkResponseDto createLink(@RequestBody DebateLinkRequestDto debateLinkRequestDto){
        return debateService.createLink(debateLinkRequestDto);
    }

    @GetMapping("/{roomId}")
    public DebateRoomResponseDto getRoom(@PathVariable String roomId){
        return debateService.getRoom(roomId);
    }

}
