package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainCategoryResDto;
import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.dto.main.OneClickResponseDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.repository.OneClickRepository;
import com.sparta.demo.repository.OneClickUserRepository;
import com.sparta.demo.util.GetIp;
import com.sparta.demo.validator.DebateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainService {
    private static final Long DEFAULT_TIMEOUT = 60L * 24 * 60;
    private static final String VISIT_COUNT = "visitCnt";

    private final DebateRepository debateRepository;
    private final OneClickRepository oneClickRepository;
    private final OneClickUserRepository oneClickUserRepository;
    private final DebateVoteRepository debateVoteRepository;
    private final DebateValidator debateValidator;

    @Autowired
    private final RedisTemplate<String, String> redisTemplate;

    // 메인 페이지 - 전체 카테고리의 HOTPEECH 목록
    public ResponseEntity<List<MainCategoryResDto>> getMainAll() {

        List<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc();

        Set<Integer> debateNum = new HashSet<>();
        while (debateNum.size() < 6) {
            debateNum.add((int) (Math.random() * debateList.size()));
        }
        Integer[] debates = new Integer[6];
        debateNum.toArray(debates);
        log.info("debateNum: {}", debateNum);

        List<MainCategoryResDto> catList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Debate debate = debateList.get(debates[i]);
            MainCategoryResDto mainCategoryResDto = new MainCategoryResDto(debate);
            catList.add(mainCategoryResDto);
        }

        return ResponseEntity.ok().body(catList);
    }

    // 카테고리 별 HOTPEECH 6개 랜덤하게 보여주기
    public ResponseEntity<List<MainCategoryResDto>> getCategoryMain(String catName) {

        log.info("catName 확인: " + catName);
        CategoryEnum category = CategoryEnum.nameOf(catName);

        // 카테고리가 전체 or 그 외 인지 구별
        if (category.toString().equals("All")) {
            return getMainAll();
        } else {
            log.info("카테고리: " + category);
            List<Debate> debateList = debateRepository.findAllByCategoryEnum(category);
            List<MainCategoryResDto> catList = new ArrayList<>();
            if (debateList.size() < 6) {                // 카테고리 별 토론 정보가 6개 미만 일시 중복허용
                Random random = new Random();
                for (int i = 0; i < 6; i++) {
                    int ran = random.nextInt(debateList.size());
                    Debate debate = debateList.get(ran);
                    MainCategoryResDto mainCategoryResDto = new MainCategoryResDto(debate);
                    catList.add(mainCategoryResDto);
                    for(int j = 0; j < i; j++) {
                        if(catList.get(i).getDebateId()==catList.get(j).getDebateId()) i--;
                        else break;
                    }
                }
            } else {
                log.info("category name : {}", category.toString());
                Set<Integer> debateNum = new HashSet<>();
                while (debateNum.size() < 6) {
                    debateNum.add((int) (Math.random() * debateList.size()));
                }
                Integer[] debates = new Integer[6];
                debateNum.toArray(debates);
                log.info("debateNum: {}", debateNum);
                for (int i = 0; i < 6; i++) {
                    Debate debate = debateList.get(debates[i]);
                    MainCategoryResDto mainCategoryResDto = new MainCategoryResDto(debate);
                    catList.add(mainCategoryResDto);
                }
            }

            return ResponseEntity.ok().body(catList);
        }
    }

    @Transactional
    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId, HttpServletRequest request) {

        String ip = GetIp.getIp(request);
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        SideTypeEnum side = debateVoteRepository.getSideByDebateIdAndIp(debateId,ip);
        if(side == null){
            side = SideTypeEnum.DEFAULT;
        }
        log.info("detail service side: {}", side);
        String redisKey = VISIT_COUNT + debateId;

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        String userIp = hashOperations.get(redisKey, VISIT_COUNT);

        if(userIp != null && userIp.equals(ip)) {
            return debateValidator.validEmptyValue(debateId, debate, side);
        }

        hashOperations.put(redisKey, VISIT_COUNT, ip);
        redisTemplate.expire(redisKey, DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        debate.setVisitCnt(debate.getVisitCnt()+1L);

        return debateValidator.validEmptyValue(debateId, debate, side);
    }

    // 원클릭 찬반 토론 가져오기
    public ResponseEntity<List<OneClickResponseDto>> getOneClick(HttpServletRequest request) {

        List<OneClick> oneClicks = oneClickRepository.findAll();
        List<OneClickResponseDto> oneClickResList = new ArrayList<>();
        String userIp = GetIp.getIp(request);


        for (OneClick oneClick: oneClicks) {
            int oneClickState = 0;
            SideTypeEnum side = oneClickUserRepository.getSideTypeEnumByOneClickIdAndUserIp(oneClick.getOneClickId(), userIp);
            if(side != null){
                oneClickState = side.getTypeNum();
            }
            OneClickResponseDto oneClickResponseDto = new OneClickResponseDto(oneClick,oneClickState);
            oneClickResList.add(oneClickResponseDto);
        }

        return ResponseEntity.ok().body(oneClickResList);
    }

    // 원클릭 토픽 찬반 선택하기
    @Transactional
    public ResponseEntity<List<OneClickResponseDto>> updateOneClickVote(SideTypeEnum sideTypeEnum,
                                                                        OneClick oneClick,
                                                                        OneClickUser oneClickUser,
                                                                        HttpServletRequest request) {

        switch (sideTypeEnum) {
            case PROS:
                oneClick.setAgreeNum(oneClick.getAgreeNum() + 1);
                oneClick.setOppoNum(oneClick.getOppoNum() - 1);
                oneClickUser.setSideTypeEnum(SideTypeEnum.PROS);
                break;
            case CONS:
                oneClick.setOppoNum(oneClick.getOppoNum() + 1);
                oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
                oneClickUser.setSideTypeEnum(SideTypeEnum.CONS);
                break;
        }

        return ResponseEntity.ok().body(getOneClick(request).getBody());
    }
    // 원클릭 토픽 찬반 선택하기
    @Transactional
    public ResponseEntity<List<OneClickResponseDto>> createOneClickVote(OneClickRequestDto oneClickRequestDto,
                                                                        String userIp,
                                                                        SideTypeEnum sideTypeEnum,
                                                                        OneClick oneClick,
                                                                        HttpServletRequest request) {

        OneClickUser oneclickUser = new OneClickUser(userIp, sideTypeEnum, oneClickRequestDto.getOneClickId());
        oneClickUserRepository.save(oneclickUser);
        switch (oneClickRequestDto.getSide()) {
            case 1:
                oneClick.setAgreeNum(oneClick.getAgreeNum() + 1);
                break;
            case 2:
                oneClick.setOppoNum(oneClick.getOppoNum() + 1);
                break;
        }

        return ResponseEntity.ok().body(getOneClick(request).getBody());
    }
}

