package com.sparta.demo.validator;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sun.jmx.remote.internal.ArrayQueue;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

        List<EnterUser> foundList = enterUserRepository.findByDebate_DebateIdOrderBySideDesc(debateId);
        ArrayDeque<EnterUser> enterUserList = new ArrayDeque<>(foundList.size());

        if(foundList.size()<2){
            enterUserList.add(foundList.get(0));
            if(foundList.get(0).getSide().equals(SideTypeEnum.PROS)){
                enterUserList.addLast(new EnterUser());
            }else enterUserList.addFirst(new EnterUser());
        }
        else{
            for (EnterUser enterUser : foundList) {
                if (enterUser.getSide().equals(SideTypeEnum.PROS) || enterUser.getSide().equals(SideTypeEnum.CONS)) {
                    enterUserList.add(enterUser);
                }
            }
            if(enterUserList.size() < 2){
                if(enterUserList.getFirst().getSide().equals(SideTypeEnum.PROS)){
                    enterUserList.addLast(new EnterUser());
                }else enterUserList.addFirst(new EnterUser());
            }
        }

        return ResponseEntity.ok().body(new MainDetailResponseDto(debate, enterUserList, side));
    }

}
