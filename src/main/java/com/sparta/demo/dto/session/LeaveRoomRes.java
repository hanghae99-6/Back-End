package com.sparta.demo.dto.session;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.EnterUser;
import lombok.Data;

@Data
public class LeaveRoomRes {
    private String userEmail;
    private String userNickName;
    private SideTypeEnum side;
    private boolean leave;

    public LeaveRoomRes(EnterUser enterUser, boolean leave) {
        this.userEmail = enterUser.getUserEmail();
        this.userNickName = enterUser.getUserNickName();
        this.side = enterUser.getSide();
        this.leave = leave;
    }
//    public LeaveRoomRes(EnterUser enterUser) {
//        this.userEmail = enterUser.getUserEmail();
//        this.userNickName = enterUser.getUserNickName();
//        this.side = enterUser.getSide();
//    }
}
