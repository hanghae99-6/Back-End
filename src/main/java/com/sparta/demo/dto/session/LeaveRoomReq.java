package com.sparta.demo.dto.session;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel("LeaveRoomRequest")
public class LeaveRoomReq {
    @ApiModelProperty(name = "방 번호", example="QB8TKZC05P")
    @NotEmpty(message = "roomId may not be empty")
    @Size(max = 50)
    private String roomId;

    @ApiModelProperty(name = "토큰", example="email 혹은 머 걍 유저네임")
    @NotEmpty(message = "유저네임 없으면 어케요")
    @Size(max = 50)
    private String token;

}
