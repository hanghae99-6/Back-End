package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import com.sparta.demo.model.Reply;
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
    private final OneClickRepository oneClickRepository;
    private final OneClickUserRepository oneClickUserRepository;
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();
    private final Map<String, CategoryEnum> categoryEnumMap = new HashMap<>();

    @Autowired
    public MainService(DebateRepository debateRepository,
                       ReplyRepository replyRepository,
                       DebateVoteRepository debateVoteRepository,
                       OneClickRepository oneClickRepository,
                       OneClickUserRepository oneClickUserRepository) {

        this.debateRepository = debateRepository;
        this.replyRepository = replyRepository;
        this.debateVoteRepository = debateVoteRepository;
        this.oneClickRepository = oneClickRepository;
        this.oneClickUserRepository = oneClickUserRepository;

        sideTypeEnumMap.put(1, SideTypeEnum.PROS);
        sideTypeEnumMap.put(2, SideTypeEnum.CONS);

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

        List<Reply> replyList = replyRepository.findAllByDebate_DebateId(debateId);

        Long totalCons = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.CONS, debateId);
        Long totalPros = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.PROS, debateId);

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto(debate, replyList, totalPros,totalCons);

        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }

    public ResponseEntity<OneClick> sumOneClick(OneClickRequestDto oneClickRequestDto, HttpServletRequest request) {
        String userIp = GetIp.getIp(request);
        int side = oneClickRequestDto.getSide();
        String oneClickTopic = oneClickRequestDto.getOneClickTopic();
        log.info("oneClickTopic : {}", oneClickTopic);
        // enum 값으로 변형
        SideTypeEnum sideTypeEnum = sideTypeEnumMap.get(side);
        // userIp 와 찬/반 정보로 OnClickUser 객체 생성
        OneClickUser oneClickUser = new OneClickUser(userIp, sideTypeEnum);
        // oneClickTopic 으로 OneClick 객체를 찾아옴
        OneClick oneClick = oneClickRepository.findByOneClickTopicAndTypeEnum(oneClickTopic, sideTypeEnum).orElseThrow(
                () -> new IllegalStateException("없는 토픽입니다.")
        );
        // 요청 유저가 누른 topic 의 OneClickUser 리스트를 뽑아오고 해당 리스트에서 각 oneClickUser 를 뽑아서 유효성 검사를 진행
        List<OneClickUser> oneClickUsers = oneClick.getOneClickUsers();
        log.info("oneClickUsers : {}", oneClickUsers);
        for (OneClickUser clickUser : oneClickUsers) {
            log.info("clickUser : {}", clickUser);
            // 요청 유저의 찬/반 중 선택한 정보와 ip 정보 객체와 동일한게 있다면 삭제하고 찬/반 구분하여 투표 수 빼기
            if (oneClickUser.getSideTypeEnum() == clickUser.getSideTypeEnum() && Objects.equals(oneClickUser.getUserIp(), clickUser.getUserIp())) {
                log.info("oneClickUser : {}", oneClickUser);
                oneClickUserRepository.delete(clickUser);
                if (side == 1) {
                    oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
                } else {
                    oneClick.setOppoNum(oneClick.getOppoNum() - 1);
                }
                return ResponseEntity.ok().body(oneClick);
            }
            // 동일한게 없다면 반대 측을 눌렀는지 확인하고 눌렀다면 요청 값이 찬성인지 반대인지에 따라 반대의 정보를 set 하고 투표 수에 더한다.
            else if (userIp.equals(clickUser.getUserIp())) {
                // TODO: 2022/05/14 DB 접근 없이 set으로 변경하는게 더 좋을까요?
                //  set 사용을 지양하라고 하는데 아래처럼 생성자를 통해 하면 되나요?
                //  생성자가 많아지지 않게 빌더를 쓰는게 낫나요?
//                oneClickUserRepository.delete(clickUser);
//                oneClickUserRepository.save(oneClickUser);
                if (side == 1) {
                    clickUser.setSideTypeEnum(SideTypeEnum.CONS);
//                    oneClick.setOppoNum(oneClick.getOppoNum() + 1);
                    oneClick = new OneClick.Builder(oneClickTopic)
                            .oppoNum(oneClick.getOppoNum() + 1)
                            .build();
                    return ResponseEntity.ok().body(oneClick);
                } else if (side == 2) {
                    clickUser.setSideTypeEnum(SideTypeEnum.PROS);
                    oneClick.setOppoNum(oneClick.getAgreeNum() + 1);
                    return ResponseEntity.ok().body(oneClick);
                }
                return ResponseEntity.ok().body(oneClick);
            }
        }
        if(sideTypeEnum == SideTypeEnum.PROS) {
            OneClick addOneClickUser = new OneClick.Builder(oneClickTopic)
                    .oneClickUsers(oneClickUsers)
                    .agreeNum(1)
                            .build();
            oneClickRepository.save(addOneClickUser);
        }

        oneClickUserRepository.save(oneClickUser);
        return ResponseEntity.ok().body(oneClick);
    }
}

