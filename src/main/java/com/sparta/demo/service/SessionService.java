package com.sparta.demo.service;

import com.sparta.demo.dto.session.EnterRes;
import com.sparta.demo.dto.session.LeaveRoomRes;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.enumeration.StatusTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.exception.ExistSessionException;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
//@RequiredArgsConstructor
public class SessionService {
    private DebateRepository debateRepository;
    private EnterUserRepository enterUserRepository;



//    private static final Long DEFAULT_TIMEOUT = 60L * 4 * 60;
//
//    private final RedisTemplate<String, String> redisTemplate;




    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

    // Collection to pair session names and OpenVidu Session objects
//    private Map<String, String> mapSessions = new ConcurrentHashMap<>();
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    // Collection to pair session names and tokens (the inner Map pairs tokens and
    // role associated)
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    // URL where our OpenVidu server is listening
    private String OPENVIDU_URL;
    // Secret shared with our OpenVidu server
    private String SECRET;

//    public SessionService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl, EnterUserRepository enterUserRepository, DebateRepository debateRepository, RedisTemplate<String, String> redisTemplate) {
//        this.debateRepository = debateRepository;
//        this.enterUserRepository = enterUserRepository;
//        this.SECRET = secret;
//        this.OPENVIDU_URL = openviduUrl;
//        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
//        this.redisTemplate = redisTemplate;
//    }
    public SessionService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl, EnterUserRepository enterUserRepository, DebateRepository debateRepository) {
        this.debateRepository = debateRepository;
        this.enterUserRepository = enterUserRepository;
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    public EnterRes enterRoom(String roomId, HttpSession httpSession, UserDetailsImpl userDetails, HttpResponse response) throws ExistSessionException, OpenViduJavaClientException, OpenViduHttpException {

        Debate debate = getDebate(roomId);
        log.info("roomId : {}, debate.getDebateId : {}", roomId, debate.getDebateId());

        EnterUser enterUser = setEnterUser(debate, userDetails.getUser());
        System.out.println("userName: " + userDetails.getUsername());

        OpenViduRole role = (getPanel(debate, userDetails.getUser().getEmail()) ? OpenViduRole.PUBLISHER:OpenViduRole.SUBSCRIBER);

        String token = getToken(userDetails.getUser(), role, roomId, httpSession);

        // todo: publisher가 모두 나가면 session 삭제하기 위한 token 저장
        // todo: 발표자(publisher)가 입장한 현황에 따라서 발표방 상태 설정
        if(role.equals(OpenViduRole.PUBLISHER)) {
//        saveToken(roomId,userEmail,token);
        setDebateStatus(debate);
        }

        boolean roomKing = debate.getUser().getEmail().equals(userDetails.getUser().getEmail());

        return new EnterRes(role, token, enterUser, debate, roomKing);

    }

    private Debate getDebate(String roomId) {
        return debateRepository.findByRoomId(roomId).get();
    }

    private EnterUser setEnterUser(Debate debate, User user) throws ExistSessionException {

        Optional<Debate> debate1 = debateRepository.findByRoomId(debate.getRoomId());

        Optional<Debate> prosCheck = debateRepository.findByRoomIdAndProsName(debate.getRoomId(),user.getEmail());
        Optional<Debate> consCheck = debateRepository.findByRoomIdAndConsName(debate.getRoomId(),user.getEmail());

        Optional<EnterUser> enterUser = Optional.of(new EnterUser());

        enterUser = enterUserRepository.findByDebate_DebateIdAndUserEmail(debate1.get().getDebateId(), user.getEmail());

        log.info("enterUser.isPresent(): {}", String.valueOf(enterUser.isPresent()));
        log.info("user 확인: {}", user.getEmail());

        if(!enterUser.isPresent()){
            if(prosCheck.isPresent()){
                enterUser = Optional.of(enterUserRepository.save(new EnterUser(debate1.get(), user, SideTypeEnum.PROS)));
            }else if(consCheck.isPresent()){
                enterUser = Optional.of(enterUserRepository.save(new EnterUser(debate1.get(), user, SideTypeEnum.CONS)));
            }else
                enterUser = Optional.of(enterUserRepository.save(new EnterUser(debate1.get(), user, SideTypeEnum.DEFAULT)));
        }

        log.info("setEnterUser enterUser.getNickname : {}", enterUser.get().getUserNickName());
        log.info("setEnterUser enterUser.getSide : {}", enterUser.get().getSide());
        return enterUser.get();
    }

    private Boolean getPanel(Debate debate, String userEmail) {

        Optional<Debate> debate1 = debateRepository.findByRoomIdAndProsName(debate.getRoomId(), userEmail);
        System.out.println("ProsName check: "+debate1.isPresent());
        Optional<Debate> debate2 = debateRepository.findByRoomIdAndConsName(debate.getRoomId(), userEmail);
        System.out.println("ConsName check: "+debate2.isPresent());

        return debate1.isPresent() || debate2.isPresent();
    }

    private String getToken(User user, OpenViduRole role, String roomId, HttpSession httpSession) throws OpenViduJavaClientException, OpenViduHttpException {
        String serverData = "{\"serverData\": \"" + user.getNickName() + "\"}";
        System.out.println("serverData : "+serverData);

        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

        String token = "";
        // 검색하는 방이 존재하지 않을 경우
        if (this.mapSessions.get(roomId) == null) {
            // session 값 생성
            Session session = this.openVidu.createSession();
            log.info("방이 없는 경우에 진입 roomId: {}, sessionId: {}", roomId,session.getSessionId());
            try{
                token = session.createConnection(connectionProperties).getToken();
                // 방 관리 map에 저장 roomId랑 들어온 유저 저장
                this.mapSessions.put(roomId, session);
                this.mapSessionNamesTokens.put(roomId, new ConcurrentHashMap<>());
                this.mapSessionNamesTokens.get(roomId).put(token, role);
            }catch (Exception e){
                httpSession.setAttribute("error",e);
            }
        }else{
            log.info("방이 있는 경우에 진입 roomId: {}, sessionId: {}", roomId, mapSessions.get(roomId).getSessionId());
            try{
                token = this.mapSessions.get(roomId).createConnection(connectionProperties).getToken();
                this.mapSessionNamesTokens.get(roomId).put(token, role);
            }catch (Exception e){
                httpSession.setAttribute("error",e);
            }
        }
        System.out.println("token :"+ token );
        return token;
    }
    // todo: 발표자(publisher)가 입장한 현황에 따라서 발표방 상태 설정
    @Transactional
    public void setDebateStatus(Debate debate){
        log.info("디베이트 상태 저장으로 진입 확인");
        boolean pros = enterUserRepository.findByDebate_DebateIdAndSide(debate.getDebateId(), SideTypeEnum.PROS).isPresent();
        boolean cons = enterUserRepository.findByDebate_DebateIdAndSide(debate.getDebateId(), SideTypeEnum.CONS).isPresent();

        if(cons && pros){
            debate.setStatusEnum(StatusTypeEnum.LIVEON);
            debateRepository.save(debate);
        }else if(pros || cons){
            debate.setStatusEnum(StatusTypeEnum.HOLD);
            debateRepository.save(debate);
        }
        log.info("debate.getStausEnum: {}", debate.getStatusEnum().getName());
    }

    // todo: publisher가 모두 나가면 session 삭제하기 위한 token 저장
//    private void saveToken(String roomId, String userEmail, String token){
//        log.info("saveToken service: {}, {}, {}", roomId, userEmail, token);
//        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
//
//        hashOperations.put(roomId, userEmail, token);
//        redisTemplate.expire(roomId, DEFAULT_TIMEOUT, TimeUnit.HOURS);
//    }



    @Transactional
    public ResponseEntity<LeaveRoomRes> leaveRoom(String roomId, String token, UserDetailsImpl userDetails) {

        EnterUser enterUser = getEnterUser(roomId, userDetails.getUser());
        Debate debate = getDebate(roomId);

        // If the session exists
        if (this.mapSessions.get(roomId) != null && this.mapSessionNamesTokens.get(roomId) != null) {
            log.info("roomId 유효성 통과");
            // If the token exists
            if (this.mapSessionNamesTokens.get(roomId).remove(token) != null) {
                log.info("token 유효성 통과");
                log.info("this.mapSessionNamesTokens.get(roomId).toString() :{}",this.mapSessionNamesTokens.get(roomId).toString());
                // todo: publisher가 모두 나가면 session 삭제
//                log.info("checkToken : {}",checkToken(roomId, enterUser.getUserEmail(), token));
//                // User left the session
//                // todo: checkToken - true면 둘 다 없음, false면 남아 있음
//                if (this.mapSessionNamesTokens.get(roomId).isEmpty() || checkToken(roomId, enterUser.getUserEmail(), token)) {
                // User left the session
                if (this.mapSessionNamesTokens.get(roomId).isEmpty()) {
                    log.info("token이 하나도 안남았을 때");
                    // Last user left: session must be removed
                    this.mapSessions.remove(roomId);
                    // todo: session이 삭제되면 토론방 상태를 완료로 변경
                    debate.setStatusEnum(StatusTypeEnum.LIVEOFF);
                    return ResponseEntity.ok().body(new LeaveRoomRes(enterUser));
                }
                return ResponseEntity.ok().body(new LeaveRoomRes(enterUser));
            } else {
                // The TOKEN wasn't valid
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            // The SESSION does not exist
            System.out.println("Problems in the app server: the SESSION does not exist");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private EnterUser getEnterUser(String roomId, User user) {
        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        return enterUserRepository.findByDebate_DebateIdAndUserEmail(debate.get().getDebateId(), user.getEmail()).get();
    }

//    private Boolean checkToken(String roomId, String userEmail, String token){
//        log.info("getSavedToken service: {}, {}, {}", roomId, userEmail, token);
//
//        Debate found = debateRepository.findByRoomId(roomId).get();
//
//        String prosEmail = found.getProsName();
//        String consEmail = found.getConsName();
//
//        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
//        String savedToken = hashOperations.get(roomId, userEmail);
//
//        if(savedToken != null && savedToken.equals(token)) {
//            hashOperations.delete(roomId,userEmail);
//            if(userEmail.equals(prosEmail)){
//                return hashOperations.get(roomId, consEmail) == null;
//            }else return hashOperations.get(roomId, prosEmail) == null;
//        }
//        return false;
//    }

}

