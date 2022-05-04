package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    final private DebateRepository debateRepository;

    public ResponseEntity<MainResponseDto> getMain() throws IOException {

        List<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc();

        MainResponseDto mainResponseDto = setMainResponseDto(debateList);


        return ResponseEntity.ok().body(mainResponseDto);
    }

    public ResponseEntity<MainResponseDto> getCatMain(String catName) throws IOException {

        log.info("service catName: {}", catName);
        List<Debate> debateList = debateRepository.findAllByCatNameContains(catName);

        MainResponseDto mainResponseDto = setMainResponseDto(debateList);


        return ResponseEntity.ok().body(mainResponseDto);
    }

    // dto에 debate 삽입하는 메서드
    private MainResponseDto setMainResponseDto(List<Debate> debateList) throws IOException {
        List<Debate> mainDebateList = new ArrayList<>();

        if (debateList.size() > 3) {
            for (int i = 0; i < 4; i++) {
                Debate debate = debateList.get(i);
                mainDebateList.add(debate);
                log.info("method catName: {}", debate.getCatName());
            }
        } else {
            for (int i = 0; i < debateList.size(); i++) {
                Debate debate = debateList.get(i);
                mainDebateList.add(debate);
                log.info("method catName: {}", debate.getCatName());
            }
        }

            MainResponseDto mainResponseDto = new MainResponseDto();
            mainResponseDto.setMainDebateList(mainDebateList);
            return mainResponseDto;
        }

    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId) {
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId);

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto();
        mainDetailResponseDto.setDebate(debate);
        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }
}

