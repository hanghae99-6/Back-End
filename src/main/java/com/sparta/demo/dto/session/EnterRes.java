package com.sparta.demo.dto.session;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import io.openvidu.java.client.OpenViduRole;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("RoomResponse")
public class EnterRes {
    private boolean user;
    private OpenViduRole role;
    private String token;
    private String nickName;
    private SideTypeEnum side;
    private String topic;
    private String content;

    public EnterRes(OpenViduRole role, String token, EnterUser enterUser, Debate debate) {
        this.user = true;
        this.role = role;
        this.token = token;
        this.nickName = enterUser.getUserNickName();
        this.side = enterUser.getSide();
        this.topic = debate.getTopic();
        this.content = debate.getContent();
    }
}
