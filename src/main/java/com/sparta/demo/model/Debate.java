package com.sparta.demo.model;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

// 홈트에서 room 레퍼런스로 참고
// implements Serializable  ??
@Setter
@Getter
@Entity
//@AllArgsConstructor
@NoArgsConstructor
public class Debate extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debateId;


    // 시작 하기 권한, 저장 권한 주기 위한 user table 연관 맵핑
    // 지금은 유저 없이 진행하기 때문에 가려뒀습니다.
//    @ManyToOne
//    @JoinColumn(name = "userId")
//    private User user;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String catName;

    // 이 부분을 유저로 받아와야할 지 고민해봤으면 좋겠습니다.
    // 지금은 user entity 없이 만드는 거라고 생각해서 그냥 String 값을 해두었습니다.
    // 추후에 user로 바꾸면 나중에 내가 참여한 토론 등에서 찾기 좀 쉽지 않을까요?(잘모르겠지만요)
    @Column(nullable = false)
    private String prosName;

    @Column(nullable = false)
    private String consName;

    @Column
    private int speechCnt;

    @Column
    private int speechMinute;

    @OneToMany(mappedBy = "debate")
    private List<Reply> replyList;

//    public Debate() {
//
//    }

    public static Debate create(DebateLinkRequestDto debateLinkRequestDto) {
        // 얘는 홈트 어쩌고 그거 그대로 긁어온건데 뭔지 모르겠네요
        // 여기서 왜인지 모르겠는데 this. 이 안먹힙니다. 그래서 생성자안에 이렇게 넣어둔것 같아요... 고칠 수 있으면 고치고싶네요ㅠ
        // todo: static(전역적으로 사용할 때) 인 경우 this를 못 쓰는걸로 알고 있습니다.
        Debate debate = new Debate();
        debate.roomId = UUID.randomUUID().toString();
        debate.topic = debateLinkRequestDto.getTopic();
        debate.catName = debateLinkRequestDto.getCategoryName();
        debate.prosName = debateLinkRequestDto.getProsName();
        debate.consName = debateLinkRequestDto.getConsName();
        debate.speechCnt = debateLinkRequestDto.getSpeechCnt();
        debate.speechMinute = debateLinkRequestDto.getSpeechMinute();
        return debate;
    }

}
