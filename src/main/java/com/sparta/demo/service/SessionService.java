package com.sparta.demo.service;

import com.sparta.demo.dto.session.EnterRes;
import com.sparta.demo.dto.session.LeaveRoomRes;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.enumeration.StatusTypeEnum;
import com.sparta.demo.exception.ExistSessionException;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
//@RequiredArgsConstructor
public class SessionService {
    private final DebateRepository debateRepository;
    private final EnterUserRepository enterUserRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 2 * 60;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DEBATE_STATUS = "debateStatus";
    private static final String ENTER_USER = "ENTER_USER";

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

    public SessionService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl, EnterUserRepository enterUserRepository, DebateRepository debateRepository, RedisTemplate<String, String> redisTemplate) {
        this.debateRepository = debateRepository;
        this.enterUserRepository = enterUserRepository;
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
        this.redisTemplate = redisTemplate;
    }

    public EnterRes enterRoom(String roomId, HttpSession httpSession, UserDetailsImpl userDetails, HttpResponse response) throws ExistSessionException, OpenViduJavaClientException, OpenViduHttpException {

        Debate debate = getDebate(roomId);
        log.info("roomId : {}, debate.getDebateId : {}", roomId, debate.getDebateId());
        User user = userDetails.getUser();
        String userEmail = userDetails.getUser().getEmail();

        EnterUser enterUser = setEnterUser(debate, user);
        System.out.println("userName: " + user.getUserName());

        OpenViduRole role = (getPanel(debate, userEmail)) ? OpenViduRole.PUBLISHER:OpenViduRole.SUBSCRIBER;

        String token = getToken(user, role, roomId, httpSession);

        // todo: publisher??? ?????? ????????? session ???????????? ?????? token ??????
        // todo: ?????????(publisher)??? ????????? ????????? ????????? ????????? ?????? ??????
        if(role.equals(OpenViduRole.PUBLISHER)) {
            log.info("PUBLISHER??? ?????? ????????????????");
            saveToken(roomId, userEmail, token);
            setDebateStatus(debate);
        }
        saveDebate(debate);

        boolean roomKing = debate.getUser().getEmail().equals(userEmail);

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
        log.info("user ??????: {}", user.getEmail());

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
        // ???????????? ?????? ???????????? ?????? ??????
        if (this.mapSessions.get(roomId) == null) {
            // session ??? ??????
            Session session = this.openVidu.createSession();
            log.info("?????? ?????? ????????? ?????? roomId: {}, sessionId: {}", roomId,session.getSessionId());
            try{
                token = session.createConnection(connectionProperties).getToken();
                // ??? ?????? map??? ?????? roomId??? ????????? ?????? ??????
                this.mapSessions.put(roomId, session);
                this.mapSessionNamesTokens.put(roomId, new ConcurrentHashMap<>());
                this.mapSessionNamesTokens.get(roomId).put(token, role);
            }catch (Exception e){
                httpSession.setAttribute("error",e);
            }
        }else{
            log.info("?????? ?????? ????????? ?????? roomId: {}, sessionId: {}", roomId, mapSessions.get(roomId).getSessionId());
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
    // todo: ?????????(publisher)??? ????????? ????????? ????????? ????????? ?????? ??????
    @Transactional
    public void setDebateStatus(Debate debate){
        log.info("???????????? ?????? ???????????? ?????? ??????");
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

    // todo: publisher??? ?????? ????????? session ???????????? ?????? token ??????
    private void saveToken(String roomId, String userEmail, String token){
        log.info("saveToken service: {}, {}, {}", roomId, userEmail, token);
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(roomId, userEmail, token);
    }

    private void saveDebate(Debate debate){
        log.info("saveDebate ??????");
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String redisKey = String.valueOf(debate.getDebateId());
        log.info("rediskey: {}", redisKey);
        hashOperations.put(redisKey, DEBATE_STATUS, debate.getStatusEnum().getName());
        log.info("?????? ??? ??? ??????: {}", hashOperations.get(redisKey, DEBATE_STATUS));
        redisTemplate.expire(redisKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }


    @Transactional
    public ResponseEntity<LeaveRoomRes> leaveRoom(String roomId, String token, UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {

        EnterUser enterUser = getEnterUser(roomId, userDetails.getUser());
        Debate debate = getDebate(roomId);

        // If the session exists
        if (this.mapSessions.get(roomId) != null && this.mapSessionNamesTokens.get(roomId) != null) {
            log.info("roomId ????????? ??????");
            // If the token exists
            if (this.mapSessionNamesTokens.get(roomId).remove(token) != null) {
                log.info("token ????????? ??????");
                // todo: publisher??? ?????? ????????? session ??????
                boolean checkToken = checkToken(roomId, enterUser.getUserEmail(), token);
                log.info("checkToken : {}",checkToken);
                // User left the session
                // todo: checkToken - true??? ??? ??? ??????, false??? ?????? ??????
                if(checkToken){
                    log.info("checkToken??? true??? ????????? ?????????????????????.");
                    this.mapSessions.remove(roomId);
                    // todo: session??? ???????????? ????????? ????????? ????????? ??????
                    debate.setStatusEnum(StatusTypeEnum.LIVEOFF);
                    return ResponseEntity.ok().body(new LeaveRoomRes(enterUser, true));
                }
                return ResponseEntity.ok().body(new LeaveRoomRes(enterUser,false));
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

    private Boolean checkToken(String roomId, String userEmail, String token){
        log.info("getSavedToken service: {}, {}, {}", roomId, userEmail, token);

        Debate found = debateRepository.findByRoomId(roomId).get();

        String prosEmail = found.getProsName();
        String consEmail = found.getConsName();

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String savedToken = hashOperations.get(roomId, userEmail);

        if(savedToken != null && savedToken.equals(token)) {
            log.info("?????? ??? ????????? ??????, ?????? ????????? ???????????? ?????? ??????, ???????????? ?????? ??? ?????? ??????");
            hashOperations.delete(roomId, userEmail);
            if(userEmail.equals(prosEmail)){
                log.info("????????? ????????? ??? ???");
                log.info("????????? ???????????? ?????????, true/ ????????? false");
                return hashOperations.get(roomId, consEmail) == null;
            }else if(userEmail.equals(consEmail)){
                log.info("????????? ???????????? ??? ???");
                log.info("????????? ???????????? ?????????, true/ ????????? false");
                return hashOperations.get(roomId, prosEmail) == null;
            }
        }
        return false;
    }

}

