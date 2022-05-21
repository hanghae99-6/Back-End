package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import lombok.Data;

@Data
public class MainCategoryResDto {

    private Long debateId;
    private String topic;
    private String categoryName;
    private Integer totalReply;
    private Long totalPros;
    private Long totalCons;

    public MainCategoryResDto(Debate debate){
        this.debateId = debate.getDebateId();
        this.topic = debate.getTopic();
        this.categoryName = debate.getCategoryEnum().getName();
        this.totalReply = debate.getTotalReply();
        this.totalPros = debate.getTotalPros();
        this.totalCons = debate.getTotalCons();
    }
}
