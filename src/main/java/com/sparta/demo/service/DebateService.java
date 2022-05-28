package com.sparta.demo.service;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateEvidence;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateEvidenceRepository;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.validator.DebateValidator;
import com.sparta.demo.validator.ErrorResult;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebateService {

    private final DebateRepository debateRepository;
    private final EnterUserRepository enterUserRepository;
    private final DebateEvidenceRepository debateEvidenceRepository;

    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto, UserDetailsImpl userDetails) {

        // String 값으로 들어온 category 이름을 위의 map 정의에서 key 값으로 삼아서 Enum 형태로 변환
        // 변환하는 이유: entity에 정의 된 값이 Enum 값이기 때문에 String 값으로는 저장이 불가능
        CategoryEnum category = CategoryEnum.nameOf(debateLinkRequestDto.getCategoryName());
        // Dto 로 들어온 값과 category로 debate entity에 값 저장
        Debate debate = Debate.create(debateLinkRequestDto, userDetails.getUser(), category);
        debateRepository.save(debate);
        // 저장된 debate의 roomId를 responseDto에 담음
        DebateLinkResponseDto debateLinkResponseDto = new DebateLinkResponseDto(debate.getRoomId());

        return ResponseEntity.ok().body(debateLinkResponseDto);
    }

    public ResponseEntity<DebateRoomResponseDto> getRoom(String roomId) {
        Debate debate = debateRepository.findByRoomId(roomId).orElseThrow(() -> new NullPointerException("존재하지 않는 방입니다."));
        return ResponseEntity.ok().body(new DebateRoomResponseDto(debate));
    }

    @Transactional
    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(String roomId, User user) {

        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        DebateRoomIdUserValidateDto debateRoomIdUserValidateDto = new DebateRoomIdUserValidateDto();
        debateRoomIdUserValidateDto.setRoomId(debate.isPresent());

        Optional<Debate> prosCheck = debateRepository.findByRoomIdAndProsName(roomId, user.getEmail());
        Optional<Debate> consCheck = debateRepository.findByRoomIdAndConsName(roomId, user.getEmail());


        Optional<EnterUser> enterUser = enterUserRepository.findByDebate_DebateIdAndUserEmail(debate.get().getDebateId(), user.getEmail());

        if (enterUser.isPresent()) {
            debateRoomIdUserValidateDto.setUser(true);
            return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
        }

        if (prosCheck.isPresent()) {
            enterUserRepository.save(new EnterUser(debate.get(), user, SideTypeEnum.PROS));
        } else if (consCheck.isPresent()) {
            enterUserRepository.save(new EnterUser(debate.get(), user, SideTypeEnum.CONS));
        }
        debateRoomIdUserValidateDto.setUser(prosCheck.isPresent() || consCheck.isPresent());

        return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
    }

    @Transactional
    public ResponseEntity<ErrorResult> saveDebateInfo(String roomId, DebateInfoDto debateInfoDto, UserDetailsImpl userDetails) {


        Optional<Debate> validRoomId = debateRepository.findByRoomId(roomId);
        if(!validRoomId.isPresent()) {
            return ResponseEntity.ok().body(new ErrorResult(false, "fail"));
        }
        Optional<EnterUser> enterUser = enterUserRepository.findByDebate_RoomId(roomId);
        if(!enterUser.isPresent()) {
            return ResponseEntity.ok().body(new ErrorResult(false, "unMatch"));
        }
        EnterUser validEnterUser = enterUser.get();
        SideTypeEnum sideTypeEnum = validEnterUser.getSide();

        DebateValidator.validateDebate(validEnterUser, userDetails, sideTypeEnum); // 유효성 검사 실행

        List<String> evidenceList = debateInfoDto.getEvidences();
        List<DebateEvidence> evidences = new ArrayList<>();

        for (String evidence : evidenceList) {
            DebateEvidence debateEvidence = new DebateEvidence(evidence, enterUser.get());
            debateEvidenceRepository.save(debateEvidence);
            evidences.add(debateEvidence);
        }

        validEnterUser.setEvidences(evidences);
        log.info("validEnterUser.getEvidences {}:" , validEnterUser.getEvidences().get(0).getEvidence());
        validEnterUser.setOpinion(debateInfoDto.getOpinion());
        log.info("validEnterUser.getOpinion(): {}",validEnterUser.getOpinion());

        return ResponseEntity.ok().body(new ErrorResult(true, "success"));
    }

    // 타이머 - 토론 시작하기
    @Transactional
    public ResponseEntity<DebateTimerRes> startDebateTimer(String roomId, UserDetailsImpl userDetails){
        Optional<Debate> debate = debateRepository.findByRoomId(roomId);

        if(!debate.isPresent()){
            throw new IllegalArgumentException("해당 토론방이 없습니다");
        } else {
            if(userDetails.getUser().getEmail().equals(debate.get().getUser().getEmail())){
                LocalDateTime localDateTime = LocalDateTime.now();
                // 토론 시작 시간
                String debateStartTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                // 토론 종료 시간
                Long debateTime = debate.get().getDebateTime();
                String debateEndTime = localDateTime.plusMinutes(debateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                DebateTimerRes debateTimerRes = new DebateTimerRes(debateStartTime, debateEndTime);
                return ResponseEntity.ok().body(debateTimerRes);
            } else throw new IllegalArgumentException("방장만 토론 타이머 시작이 가능합니다.");
        }
    }
}
