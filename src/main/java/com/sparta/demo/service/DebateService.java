package com.sparta.demo.service;

import com.sparta.demo.dto.debate.DebateInfoDto;
import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateEvidence;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateEvidenceRepository;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.repository.UserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.validator.DebateValidator;
import com.sparta.demo.validator.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private final UserRepository userRepository;

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

    @Transactional
    public ResponseEntity<ErrorResult> saveDebateInfo(String roomId, DebateInfoDto debateInfoDto, UserDetailsImpl userDetails) {


        Optional<Debate> validRoomId = debateRepository.findByRoomId(roomId);
        if(!validRoomId.isPresent()) {
            return ResponseEntity.ok().body(new ErrorResult(false, "fail"));
        }
        Optional<EnterUser> enterUser = enterUserRepository.findByDebate_DebateIdAndUserEmail(validRoomId.get().getDebateId(), userDetails.getUser().getEmail());
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

    public ResponseEntity<ErrorResult> emailCheck(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return ResponseEntity.ok().body(new ErrorResult(user.isPresent(),"emailChecking"));

    }
}
