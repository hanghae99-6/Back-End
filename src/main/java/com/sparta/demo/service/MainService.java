package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.dto.main.OneClickResponseDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.OneClickRepository;
import com.sparta.demo.repository.OneClickUserRepository;
import com.sparta.demo.util.GetIp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class MainService {

    final private DebateRepository debateRepository;
    private final OneClickRepository oneClickRepository;
    private final OneClickUserRepository oneClickUserRepository;
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();
    private final Map<String, CategoryEnum> categoryEnumMap = new HashMap<>();

    @Autowired
    public MainService(DebateRepository debateRepository,
                       OneClickRepository oneClickRepository,
                       OneClickUserRepository oneClickUserRepository) {

        this.debateRepository = debateRepository;
        this.oneClickRepository = oneClickRepository;
        this.oneClickUserRepository = oneClickUserRepository;

        sideTypeEnumMap.put(1, SideTypeEnum.PROS);
        sideTypeEnumMap.put(2, SideTypeEnum.CONS);

        categoryEnumMap.put("전체", CategoryEnum.All); categoryEnumMap.put("정치",CategoryEnum.POLITICS); categoryEnumMap.put("경제",CategoryEnum.ECONOMY);
        categoryEnumMap.put("사회",CategoryEnum.SOCIETY); categoryEnumMap.put("일상",CategoryEnum.DAILY); categoryEnumMap.put("문화예술",CategoryEnum.CULTURE);
        categoryEnumMap.put("IT과학",CategoryEnum.SCIENCE); categoryEnumMap.put("해외토픽",CategoryEnum.GLOBAL); categoryEnumMap.put("기타",CategoryEnum.ETC);
    }

    // 메인 페이지 - 전체 카테고리의 hot peech
    public ResponseEntity<MainResponseDto> getMain(){

        List<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc();

        Set<Integer> debateNum = new HashSet<>();
        while(debateNum.size()<6){
            debateNum.add((int)(Math.random() * debateList.size()));
        }
        Integer[] debates = new Integer[6];
        debateNum.toArray(debates);
        log.info("debateNum: {}", debateNum);

        List<Debate> arr = new ArrayList<>();
        for(int i=0; i<6;i++){
            arr.add(debateList.get(debates[i]));
        }
        MainResponseDto mainResponseDto = new MainResponseDto(arr);
        return ResponseEntity.ok().body(mainResponseDto);
    }

    // 카테고리 별 wepeech 6개 랜덤하게 보여주기기
     public ResponseEntity<MainResponseDto> getCatMain(String catName){

        log.info("catName 확인: " + catName);
        CategoryEnum category = CategoryEnum.valueOf(String.valueOf(categoryEnumMap.get(catName)));

        // 카테고리가 전체 or 그 외 인지 구별
        if (category.toString().equals("All")){
            return getMain();
        }
        else {
            log.info("카테고리: " + category);
            List<Debate> debateList = debateRepository.findAllByCategoryEnum(category);
            List<Debate> arr = new ArrayList<>();
            if(debateList.size()<6){                // 카테고리 별 토론 정보가 6개 미만 일시 중복허용
                Random random = new Random();
                for(int i=0; i<6;i++){
                    int ran = random.nextInt(debateList.size());
                    arr.add(debateList.get(ran));
                }
            } else{
                Set<Integer> debateNum = new HashSet<>();
                while(debateNum.size()<6){
                    debateNum.add((int)(Math.random() * debateList.size()));
                }
                Integer[] debates = new Integer[6];
                debateNum.toArray(debates);
                log.info("debateNum: {}", debateNum);
                for(int i=0; i<6;i++){
                    arr.add(debateList.get(debates[i]));
                }
            }
            MainResponseDto mainResponseDto = new MainResponseDto(arr);
            return ResponseEntity.ok().body(mainResponseDto);
        }
    }


    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId) {
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto(debate, debate.getEnterUserList());

        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }

    // 원클릭 찬반 토론 가져오기
    public ResponseEntity<List<OneClickResponseDto>> getOneClick(HttpServletRequest request) {

        List<OneClick> oneClicks = oneClickRepository.findAll();
        List<OneClickResponseDto> oneClickResList = new ArrayList<>();
        String userIp = GetIp.getIp(request);

        for(OneClick oneClick : oneClicks) {
            int oneClickState = 0;
            List<OneClickUser> oneClickUsers = oneClick.getOneClickUsers();
            log.info("oneClickUsers.size() : {}", oneClickUsers.size());
            for(OneClickUser oneClickUser : oneClickUsers){
                if (userIp.equals(oneClickUser.getUserIp())) {
                    oneClickState = (oneClickUser.getSideTypeEnum() == SideTypeEnum.PROS)? 1 : 2;
                    break;
                }
            }
            OneClickResponseDto oneClickRes = new OneClickResponseDto(oneClick, oneClickState);
            oneClickResList.add(oneClickRes);
        }
        return ResponseEntity.ok().body(oneClickResList);
    }

    // 원클릭 토픽 찬반 선택하기
    @Transactional
    public ResponseEntity<List<OneClickResponseDto>> sumOneClick(OneClickRequestDto oneClickRequestDto, HttpServletRequest request) {

        String userIp = GetIp.getIp(request);
        int side = oneClickRequestDto.getSide();
        Long oneClickId = oneClickRequestDto.getOneClickId();
        // enum 값으로 변형
        SideTypeEnum sideTypeEnum = sideTypeEnumMap.get(side);
        // oneClickTopic 으로 OneClick 객체를 찾아옴
        OneClick oneClick = oneClickRepository.findById(oneClickId).orElseThrow(
                () -> new IllegalStateException("없는 토픽입니다.")
        );
        List<OneClickUser> oneClickUsers = oneClick.getOneClickUsers();
        log.info("oneClickUsers.size() : {}", oneClickUsers.size());
        for (OneClickUser oneClickUser : oneClickUsers){
            log.info("oneClickUser.getUserIp() : {}", oneClickUser.getUserIp());
            if (userIp.equals(oneClickUser.getUserIp())) {
                if(oneClickUser.getSideTypeEnum() != sideTypeEnum) {
                    oneClickUserRepository.delete(oneClickUser);
                    // 유저가 선택한게 찬성이라면 토픽의 찬성 수에 +1, 반대 수에 -1
                    if(sideTypeEnum == SideTypeEnum.PROS) {
                        oneClick.setAgreeNum(oneClick.getAgreeNum() + 1);
                        oneClick.setOppoNum(oneClick.getOppoNum() - 1);
                    } else {
                        oneClick.setOppoNum(oneClick.getOppoNum() + 1);
                        oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
                    }
                    oneClickUser.setSideTypeEnum(sideTypeEnum);
                    // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
                    List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();
                    return ResponseEntity.ok().body(oneClickResDtoList);
                } else {
                    log.info("oneClickUser.getSideTypeEnum() : {}", oneClickUser.getSideTypeEnum());
                    oneClickUserRepository.delete(oneClickUser);
                    if(sideTypeEnum == SideTypeEnum.PROS) {
                        oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
                    } else {
                        oneClick.setOppoNum(oneClick.getOppoNum() - 1);
                    }
                    // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
                    List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();

                    return ResponseEntity.ok().body(oneClickResDtoList);
                }
            }
        }
//        // oneClickUser 에 유저 IP와 oneClickId로 찾는다.
//        Optional<OneClickUser> optionalOneClickUser = oneClickUserRepository.findByUserIpAndOneClickId(userIp, oneClickId);
//        // 있다면 찬성인지 반대인지 확인하고 현재 유저가 선택한 side 와 다르다면(찬성을 눌렀던 사람이 반대를 누름) 기존 정보를 삭제
//        // 같은 side 를 눌렀다면 중복 투표 에러 발생
//        if(optionalOneClickUser.isPresent()) {
//            if(optionalOneClickUser.get().getSideTypeEnum() != sideTypeEnum) {
//                oneClickUserRepository.delete(optionalOneClickUser.get());
//                // 유저가 선택한게 찬성이라면 토픽의 찬성 수에 +1, 반대 수에 -1
//                if(sideTypeEnum == SideTypeEnum.PROS) {
//                    oneClick.setAgreeNum(oneClick.getAgreeNum() + 1);
//                    oneClick.setOppoNum(oneClick.getOppoNum() - 1);
//                } else {
//                    oneClick.setOppoNum(oneClick.getOppoNum() + 1);
//                    oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
//                }
//                OneClickUser oneClickUser = new OneClickUser(userIp, sideTypeEnum, oneClickId);
//                oneClickUserRepository.save(oneClickUser);
//                // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
//                List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();
//
//                return ResponseEntity.ok().body(oneClickResDtoList);
//            } else {
//                oneClickUserRepository.delete(optionalOneClickUser.get());
//                if(sideTypeEnum == SideTypeEnum.PROS) {
//                    oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
//                } else {
//                    oneClick.setOppoNum(oneClick.getOppoNum() - 1);
//                }
//                // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
//                List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();
//
//                return ResponseEntity.ok().body(oneClickResDtoList);
//            }
//        }
        // 기존에 누른게 없다면 userIp 와 찬/반 정보, 토픽 Id 로 OnClickUser 객체 생성 및 저장
        OneClickUser oneClickUser = new OneClickUser(userIp, sideTypeEnum, oneClickId);
        oneClickUserRepository.save(oneClickUser);
        // 유저가 선택한게 찬성이라면 토픽의 찬성 수에 +1
        if(sideTypeEnum == SideTypeEnum.PROS) {
            oneClick.setAgreeNum(oneClick.getAgreeNum() + 1);
        } else {
            oneClick.setOppoNum(oneClick.getOppoNum() + 1);
        }
        // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
        List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();

        return ResponseEntity.ok().body(oneClickResDtoList);
    }
}

