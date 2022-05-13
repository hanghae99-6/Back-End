package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.repository.OneClickRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
//@RequiredArgsConstructor
public class MainService {

    final private DebateRepository debateRepository;
    final private ReplyRepository replyRepository;
    final private DebateVoteRepository debateVoteRepository;
    private final OneClickRepository oneClickRepository;
    private final Map<String, CategoryEnum> categoryEnumMap = new HashMap<>();

    @Autowired
    public MainService(DebateRepository debateRepository, ReplyRepository replyRepository, DebateVoteRepository debateVoteRepository, OneClickRepository oneClickRepository) {
        this.debateRepository = debateRepository;
        this.replyRepository = replyRepository;
        this.debateVoteRepository = debateVoteRepository;
        this.oneClickRepository = oneClickRepository;

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

        Optional<OneClick> optionalOneClick = oneClickRepository.findByUserIpAndOneClickTopic(userIp, oneClickTopic);
        if(!optionalOneClick.isPresent()){
            throw new IllegalArgumentException("이미 선택한 토픽입니다.");
        }

        return null;
    }
}

