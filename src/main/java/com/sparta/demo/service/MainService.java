package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.*;
import com.sparta.demo.repository.*;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.*;

@Slf4j
@Service
public class MainService {

    final private DebateRepository debateRepository;
    final private ReplyRepository replyRepository;
    final private DebateVoteRepository debateVoteRepository;
    final private EnterUserRepository enterUserRepository;
    private final OneClickRepository oneClickRepository;
    private final OneClickUserRepository oneClickUserRepository;
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();
    private final Map<String, CategoryEnum> categoryEnumMap = new HashMap<>();

    @Autowired
    public MainService(DebateRepository debateRepository,
                       ReplyRepository replyRepository,
                       DebateVoteRepository debateVoteRepository,
                       EnterUserRepository enterUserRepository,
                       OneClickRepository oneClickRepository,
                       OneClickUserRepository oneClickUserRepository) {

        this.debateRepository = debateRepository;
        this.replyRepository = replyRepository;
        this.debateVoteRepository = debateVoteRepository;
        this.enterUserRepository = enterUserRepository;
        this.oneClickRepository = oneClickRepository;
        this.oneClickUserRepository = oneClickUserRepository;

        sideTypeEnumMap.put(0, SideTypeEnum.PROS);
        sideTypeEnumMap.put(1, SideTypeEnum.CONS);

        categoryEnumMap.put("ALL", CategoryEnum.All); categoryEnumMap.put("정치",CategoryEnum.POLITICS); categoryEnumMap.put("경제",CategoryEnum.ECONOMY);
        categoryEnumMap.put("사회",CategoryEnum.SOCIETY); categoryEnumMap.put("일상",CategoryEnum.DAILY); categoryEnumMap.put("생활문화",CategoryEnum.CULTURE);
        categoryEnumMap.put("IT/과학",CategoryEnum.SCIENCE); categoryEnumMap.put("해외토픽",CategoryEnum.GLOBAL); categoryEnumMap.put("기타",CategoryEnum.ETC);
    }

    public ResponseEntity<List<OneClick>> getOneClick() {
        List<OneClick> oneClicks = oneClickRepository.findAll();
        return ResponseEntity.ok().body(oneClicks);
    }

    public ResponseEntity<MainResponseDto> getMain(){
//        Pageable pageable = Pageable.ofSize(4);

        List<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc();

        ArrayList<Debate> arr = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<8;i++){
            int ran = random.nextInt(debateList.size());
            arr.add(debateList.get(ran));
//            for(int j=0; j<i; j++){               // 8 개 이상일 경우 중복 허용 안하기.
//                if(arr.get(i)==arr.get(j)) i--;
//            }
        }

        MainResponseDto mainResponseDto = new MainResponseDto(arr);

        return ResponseEntity.ok().body(mainResponseDto);
    }

    public ResponseEntity<MainResponseDto> getCatMain(String catName){

        log.info("catName 확인: " + catName);
        CategoryEnum category = CategoryEnum.valueOf(String.valueOf(categoryEnumMap.get(catName)));

        log.info("카테고리: " + category);

        List<Debate> debateList = debateRepository.findAllByCategoryEnum(category);

        ArrayList<Debate> arr2 = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<8;i++){
            int ran = random.nextInt(debateList.size());
            arr2.add(debateList.get(ran));
        }

        MainResponseDto mainResponseDto = new MainResponseDto(arr2);

        return ResponseEntity.ok().body(mainResponseDto);
    }


    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId) {
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        List<EnterUser> enterUserList = enterUserRepository.findAllByDebate_DebateId(debateId);

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto(debate, enterUserList);

        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }

    public ResponseEntity<OneClick> sumOneClick(OneClickRequestDto oneClickRequestDto, HttpServletRequest request) {
        String userIp = GetIp.getIp(request);
        int side = oneClickRequestDto.getSide();
        Long oneClickId = oneClickRequestDto.getOneClickId();
        log.info("oneClickTopic : {}", oneClickId);
        // enum 값으로 변형
        SideTypeEnum sideTypeEnum = sideTypeEnumMap.get(side);
        // oneClickTopic 으로 OneClick 객체를 찾아옴
        OneClick oneClick = oneClickRepository.findById(oneClickId).orElseThrow(
                () -> new IllegalStateException("없는 토픽입니다.")
        );

        Optional<OneClickUser> optionalOneClickUser = oneClickUserRepository.findByUserIpAndOneClickId(userIp, oneClickId);
        if(optionalOneClickUser.isPresent()) {
            if(optionalOneClickUser.get().getSideTypeEnum() != sideTypeEnum) {
                oneClickUserRepository.delete(optionalOneClickUser.get());
            } else {
                throw new IllegalArgumentException("투표는 중복되지 않습니다.");
            }
        }
        // userIp 와 찬/반 정보로 OnClickUser 객체 생성 및 저장
        OneClickUser oneClickUser = new OneClickUser(userIp, sideTypeEnum, oneClickId);
        oneClickUserRepository.save(oneClickUser);

        List<OneClickUser> clickUsers = oneClickUserRepository.findByOneClickIdAndSideTypeEnum(oneClickId, sideTypeEnum);
        if(sideTypeEnum == SideTypeEnum.PROS) {
            oneClick.setAgreeNum(clickUsers.size());
            oneClickRepository.save(oneClick);
        } else {
            oneClick.setOppoNum(clickUsers.size());
            oneClickRepository.save(oneClick);
        }
        return ResponseEntity.ok().body(oneClick);
    }
}

