package com.sparta.demo.validator;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DebateValidator {

    private final EnterUserRepository enterUserRepository;

    public static void validateDebate(EnterUser validEnterUser, UserDetailsImpl userDetails, SideTypeEnum sideTypeEnum) {

        if(!validEnterUser.getUserEmail().equals(userDetails.getUser().getEmail())) {
            throw new IllegalArgumentException("토론자가 아닙니다.");
        }

        if(!validEnterUser.getSide().equals(sideTypeEnum)) {
            throw new IllegalArgumentException("입장이 맞지 않습니다.");
        }
    }

    public ResponseEntity<MainDetailResponseDto> validEmptyValue(Long debateId, Debate debate, SideTypeEnum side) {

        List<EnterUser> enterUserList = enterUserRepository.findByDebate_DebateIdOrderBySideDesc(debateId);
        // 토론방에 상대자가 들어오지 않았을 경우 상세페이지에 빈값 보내주기
        if(enterUserList.size()<2){
            EnterUser enterUser = new EnterUser();
            if(enterUserList.get(0).getSide().getTypeNum()==1)
                enterUserList.add(enterUser);
            else
                enterUserList.add(0,enterUser);
        }
        return ResponseEntity.ok().body(new MainDetailResponseDto(debate, enterUserList, side));
    }

}
