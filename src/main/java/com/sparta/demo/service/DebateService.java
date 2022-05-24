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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
        Debate debate = debateRepository.findByRoomId(roomId).orElseThrow(()->new NullPointerException("존재하지 않는 방입니다."));
        return ResponseEntity.ok().body(new DebateRoomResponseDto(debate));
    }

    @Transactional
    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(String roomId, User user) {

        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        DebateRoomIdUserValidateDto debateRoomIdUserValidateDto = new DebateRoomIdUserValidateDto();
        debateRoomIdUserValidateDto.setRoomId(debate.isPresent());

        Optional<Debate> prosCheck = debateRepository.findByRoomIdAndProsName(roomId,user.getEmail());
        Optional<Debate> consCheck = debateRepository.findByRoomIdAndConsName(roomId,user.getEmail());


        Optional<EnterUser> enterUser = enterUserRepository.findByDebate_DebateIdAndUserEmail(debate.get().getDebateId(), user.getEmail());

        if(enterUser.isPresent()){
            debateRoomIdUserValidateDto.setUser(true);
            return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
        }

        if(prosCheck.isPresent()){
            enterUserRepository.save(new EnterUser(debate.get(), user, SideTypeEnum.PROS));
        }
        else if(consCheck.isPresent()){
            enterUserRepository.save(new EnterUser(debate.get(), user, SideTypeEnum.CONS));
        }
        debateRoomIdUserValidateDto.setUser(prosCheck.isPresent() || consCheck.isPresent());

        return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
    }

    @Transactional
    public ErrorResult saveDebateInfo(String roomId, DebateInfoDto debateInfoDto, UserDetailsImpl userDetails) {

        int sideNum = (debateInfoDto.getProsCons().equals("찬성"))? 1 : 2;
        SideTypeEnum sideTypeEnum = SideTypeEnum.typeOf(sideNum);

        Optional<Debate> validRoomId = debateRepository.findByRoomId(roomId);
        if(!validRoomId.isPresent()) {
            return new ErrorResult(false, "fail");
        }
        Optional<EnterUser> enterUser = enterUserRepository.findBySideAndDebate_RoomId(sideTypeEnum, roomId);
        if(!enterUser.isPresent()) {
            return new ErrorResult(false, "unMatch");
        }
        EnterUser validEnterUser = enterUser.get();

        DebateValidator.validateDebate(validEnterUser, userDetails, sideTypeEnum); // 유효성 검사 실행

        List<String> evidenceList = debateInfoDto.getEvidences();
        List<DebateEvidence> evidences = new ArrayList<>();

        for (String evidence : evidenceList) {
            DebateEvidence debateEvidence = new DebateEvidence(evidence, enterUser.get());
            debateEvidenceRepository.save(debateEvidence);
            evidences.add(debateEvidence);
        }

        validEnterUser.setEvidences(evidences);
        validEnterUser.setOpinion(debateInfoDto.getOpinion());

        return new ErrorResult(true, "success");
    }

}
