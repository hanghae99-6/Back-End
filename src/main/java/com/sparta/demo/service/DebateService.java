package com.sparta.demo.service;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateEvidence;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.repository.DebateEvidenceRepository;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.validator.DebateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class DebateService {

    private final DebateRepository debateRepository;
    private final Map<String, CategoryEnum> categoryEnumMap = new HashMap<>();
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();
    private final EnterUserRepository enterUserRepository;
    private final DebateEvidenceRepository debateEvidenceRepository;

    @Autowired
    public DebateService(DebateRepository debateRepository,
                         EnterUserRepository enterUserRepository,
                         DebateEvidenceRepository debateEvidenceRepository) {
        this.debateRepository = debateRepository;
        this.enterUserRepository = enterUserRepository;
        this.debateEvidenceRepository = debateEvidenceRepository;

        sideTypeEnumMap.put(1, SideTypeEnum.PROS);
        sideTypeEnumMap.put(2, SideTypeEnum.CONS);

        // categoryEnum 을 Map 형태로 정의 해서 String 으로 들어온 key 값에 대한 Enum 값 정의
        categoryEnumMap.put("정치",CategoryEnum.POLITICS); categoryEnumMap.put("경제",CategoryEnum.ECONOMY);
        categoryEnumMap.put("사회",CategoryEnum.SOCIETY); categoryEnumMap.put("일상",CategoryEnum.DAILY); categoryEnumMap.put("문화예술",CategoryEnum.CULTURE);
        categoryEnumMap.put("IT과학",CategoryEnum.SCIENCE); categoryEnumMap.put("해외토픽",CategoryEnum.GLOBAL); categoryEnumMap.put("기타",CategoryEnum.ETC);
    }

    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto, UserDetailsImpl userDetails) {

        // String 값으로 들어온 category 이름을 위의 map 정의에서 key 값으로 삼아서 Enum 형태로 변환
        // 변환하는 이유: entity에 정의 된 값이 Enum 값이기 때문에 String 값으로는 저장이 불가능
        CategoryEnum category = categoryEnumMap.get(debateLinkRequestDto.getCategoryName());
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
    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(String roomId, String userEmail) {

        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        DebateRoomIdUserValidateDto debateRoomIdUserValidateDto = new DebateRoomIdUserValidateDto();
        debateRoomIdUserValidateDto.setRoomId(debate.isPresent());

        Optional<Debate> prosCheck = debateRepository.findByRoomIdAndProsName(roomId,userEmail);
        Optional<Debate> consCheck = debateRepository.findByRoomIdAndConsName(roomId,userEmail);


        if(prosCheck.isPresent()){
            enterUserRepository.save(new EnterUser(debate.get(),userEmail, SideTypeEnum.PROS));
        }else if(consCheck.isPresent()){
           enterUserRepository.save(new EnterUser(debate.get(),userEmail, SideTypeEnum.CONS));
        }
        debateRoomIdUserValidateDto.setUser(prosCheck.isPresent() || consCheck.isPresent());

        return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
    }

    @Transactional
    public ResponseEntity<Boolean> saveDebateInfo(String roomId, DebateInfoDto debateInfoDto, UserDetailsImpl userDetails) {

        int sideNum = (debateInfoDto.getProsCons().equals("찬성"))? 1 : 2;
        SideTypeEnum sideTypeEnum = sideTypeEnumMap.get(sideNum);

        EnterUser validEnterUser = enterUserRepository.findBySideAndDebate_RoomId(sideTypeEnum, roomId).orElseThrow(
                () -> new IllegalArgumentException("토론방이 없습니다.")
        );

        DebateValidator.validateDebate(validEnterUser, userDetails, sideTypeEnum); // 유효성 검사 실행

        List<String> evidenceList = debateInfoDto.getEvidences();
        List<DebateEvidence> evidences = new ArrayList<>();

        for (String evidence : evidenceList) {
            DebateEvidence debateEvidence = new DebateEvidence(evidence);
            debateEvidenceRepository.save(debateEvidence);
            evidences.add(debateEvidence);
        }

        validEnterUser.setEvidences(evidences);
        validEnterUser.setOpinion(debateInfoDto.getOpinion());

        return ResponseEntity.ok().body(true);
    }

}
