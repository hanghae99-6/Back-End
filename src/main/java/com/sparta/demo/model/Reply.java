package com.sparta.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Reply extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String reply;

    // TODO: 추후에 user과 합치면 주석 해제(연관관계 주의)
//    @ManyToOne
//    @JoinColumn(name = "userId")
//    private User user;

    // TODO: 그냥 debateId만 넣는걸로 변경(연관관계 해제)
    @ManyToOne
    @JoinColumn(name = "debateId")
    private Debate debate;

    public Reply(String reply, Debate debate) {
        this.reply = reply;
        this.debate = debate;
    }
}
