package com.sparta.demo.validator;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.security.UserDetailsImpl;
import org.springframework.stereotype.Component;

@Component
public class DebateValidator {

    public static void validateDebate(EnterUser validEnterUser, UserDetailsImpl userDetails, SideTypeEnum sideTypeEnum) {

        if(!validEnterUser.getUserEmail().equals(userDetails.getUser().getEmail())) {
            throw new IllegalArgumentException("토론자가 아닙니다.");
        }

        if(!validEnterUser.getSide().equals(sideTypeEnum)) {
            throw new IllegalArgumentException("입장이 맞지 않습니다.");
        }
    }

}
