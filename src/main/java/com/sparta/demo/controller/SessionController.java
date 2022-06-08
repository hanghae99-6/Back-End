package com.sparta.demo.controller;

import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.sparta.demo.dto.session.EnterRes;
import com.sparta.demo.dto.session.LeaveRoomReq;
import com.sparta.demo.dto.session.LeaveRoomRes;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.SessionService;
import com.sparta.demo.exception.ExistSessionException;
import com.sparta.demo.exception.MaxPublisherException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@Api(value = "방 관리 API", tags = {"Room"})
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/{roomId}")
    @ApiOperation(value = "입장했을 때 유효성 검사", notes = "<strong>입장하기</strong> roomId와 token의 유저 정보를 통해 토론자인지 아닌지 확인 후 role 전송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "입장 성공"),
            @ApiResponse(code = 400, message = "input 오류", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "토큰 만료 or 토큰 없음 or 토큰 오류 -> 권한 인증 오류", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "방 정보가 없습니다.", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
    })
    public ResponseEntity<EnterRes> enterRoom(@PathVariable String roomId,
                                              HttpSession httpSession,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              HttpResponse response) throws OpenViduJavaClientException, OpenViduHttpException, ExistSessionException, MaxPublisherException {

        System.out.println("/api/rooms/{roomId} 진입 확인");
        log.info("roomId : {}", roomId);
        log.info("userEmail : {}", userDetails.getUser().getEmail());

        return ResponseEntity.ok().body(sessionService.enterRoom(roomId, httpSession, userDetails, response));
    }


    @PutMapping("")
    @ApiOperation(value = "참가자가 방을 나갈 경우 사용", notes = "<strong>방 나가기</strong>를 통해 방 정보 LIVEOFF로 변경 및 방 관리 map에서 해당 정보 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "방 나가기 성공"),
            @ApiResponse(code = 400, message = "input 오류", response = ErrorResponse.class),
            @ApiResponse(code = 401, message = "토큰 만료 or 토큰 없음 or 토큰 오류 -> 권한 인증 오류", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "방 정보가 없습니다.", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
    })
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<LeaveRoomRes> leaveRoom(@RequestBody LeaveRoomReq leaveRoomReq, @AuthenticationPrincipal UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        System.out.println("/api/rooms 방 나가기 진입");
        String roomId = leaveRoomReq.getRoomId();
        String token = leaveRoomReq.getToken();
        log.info("roomId: {}, token: {}", roomId, token);

        return sessionService.leaveRoom(roomId, token, userDetails);

    }
}
