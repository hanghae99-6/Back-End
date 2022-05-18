package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.dto.main.OneClickResponseDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import com.sparta.demo.repository.*;
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

        categoryEnumMap.put("전체", CategoryEnum.All); categoryEnumMap.put("정치",CategoryEnum.POLITICS); categoryEnumMap.put("경제",CategoryEnum.ECONOMY);
        categoryEnumMap.put("사회",CategoryEnum.SOCIETY); categoryEnumMap.put("일상",CategoryEnum.DAILY); categoryEnumMap.put("문화예술",CategoryEnum.CULTURE);
        categoryEnumMap.put("IT과학",CategoryEnum.SCIENCE); categoryEnumMap.put("해외토픽",CategoryEnum.GLOBAL); categoryEnumMap.put("기타",CategoryEnum.ETC);
    }

    public ResponseEntity<List<OneClick>> getOneClick() {
        List<OneClick> oneClicks = oneClickRepository.findAll();
        return ResponseEntity.ok().body(oneClicks);
    }

    // 메인 페이지 - 전체 카테고리의 hot peech
    public ResponseEntity<MainResponseDto> getMain(){

        List<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc();

        ArrayList<Debate> arr = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<6;i++){
            int ran = random.nextInt(debateList.size());
            arr.add(debateList.get(ran));
            // 8개 이상일 경우만 중복 허용 x
//            for (int j = 0; j < i; j++) {
//                if (arr.get(i).getDebateId()==arr.get(j).getDebateId()) i--;
//                else break;
//            }

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
            ArrayList<Debate> arr2 = new ArrayList<>();

            Random random = new Random();
            for(int i=0; i<6;i++){
                int ran = random.nextInt(debateList.size());
                arr2.add(debateList.get(ran));
                // 6개 이상일 경우만 중복 허용 x
//                for (int j = 0; j < i; j++) {
//                    if (arr2.get(i).equals(arr2.get(j))) i--;
//                    else break;
//                }
            }
            MainResponseDto mainResponseDto = new MainResponseDto(arr2);
            return ResponseEntity.ok().body(mainResponseDto);
        }
    }


    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId) {
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        List<EnterUser> enterUserList = enterUserRepository.findAllByDebate_DebateId(debateId);

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto(debate, enterUserList);

        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }

    // 원클릭 토픽 가져오기
    public ResponseEntity<List<OneClickResponseDto>> getOneClick(HttpServletRequest request) {

        List<OneClick> oneClicks = oneClickRepository.findAll();
        List<OneClickResponseDto> oneClickResList = new ArrayList<>();
        String userIp = GetIp.getIp(request);

        for(OneClick oneClick : oneClicks) {
            int oneClickState = 2;
            List<OneClickUser> oneClickUsers = oneClickUserRepository.findByOneClickId(oneClick.getOneClickId());
            for(OneClickUser oneClickUser : oneClickUsers){
                if (userIp.equals(oneClickUser.getUserIp())) {
                    oneClickState = (oneClickUser.getSideTypeEnum() == SideTypeEnum.PROS)? 0 : 1;
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

        // oneClickUser 에 유저 IP와 oneClickId로 찾는다.
        Optional<OneClickUser> optionalOneClickUser = oneClickUserRepository.findByUserIpAndOneClickId(userIp, oneClickId);
        // 있다면 찬성인지 반대인지 확인하고 현재 유저가 선택한 side 와 다르다면(찬성을 눌렀던 사람이 반대를 누름) 기존 정보를 삭제
        // 같은 side 를 눌렀다면 중복 투표 에러 발생
        if(optionalOneClickUser.isPresent()) {
            if(optionalOneClickUser.get().getSideTypeEnum() != sideTypeEnum) {
                oneClickUserRepository.delete(optionalOneClickUser.get());
            } else {
                throw new IllegalArgumentException("투표는 중복되지 않습니다.");
            }
        }
        // 기존에 누른게 없다면 userIp 와 찬/반 정보, 토픽 Id 로 OnClickUser 객체 생성 및 저장
        OneClickUser oneClickUser = new OneClickUser(userIp, sideTypeEnum, oneClickId);
        oneClickUserRepository.save(oneClickUser);
        // 토픽에서 유저가 선택한 side 를 선택한 유저 리스트를 불러온다.
        List<OneClickUser> clickUsers = oneClickUserRepository.findByOneClickIdAndSideTypeEnum(oneClickId, sideTypeEnum);
        // 유저가 선택한게 찬성이라면 토픽의 찬성 수에 찬성을 선택한 유저 수를 set 하고 상태는 0으로 Set 한 후 해당 상태를 저장한다.
        if(sideTypeEnum == SideTypeEnum.PROS) {
            oneClick.setAgreeNum(clickUsers.size());
            oneClick.setOppoNum(oneClick.getOppoNum() - 1);
            oneClick.setOneClickState(0);
            oneClickRepository.save(oneClick);
        } else {
            oneClick.setOppoNum(clickUsers.size());
            oneClick.setAgreeNum(oneClick.getAgreeNum() - 1);
            oneClick.setOneClickState(1);
            oneClickRepository.save(oneClick);
        }
        // 원클릭 찬반 토론 전체 데이터를 보내기 위해 GetOneClick 메소드 사용
        List<OneClickResponseDto> oneClickResDtoList = getOneClick(request).getBody();

        return ResponseEntity.ok().body(oneClickResDtoList);
    }
}

