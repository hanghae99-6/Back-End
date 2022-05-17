package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.enumeration.CategoryEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

// 홈트에서 room 레퍼런스로 참고
// implements Serializable  ??
@Setter
@Getter
@Entity
@NoArgsConstructor
public class Debate extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
//    @NotNull
//    @JsonBackReference
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    @Enumerated(value=EnumType.STRING)
    private CategoryEnum categoryEnum;

    @Column(nullable = false)
    private String prosName;

    @Column(nullable = false)
    private String consName;

    @Column
    private String content;

    @OneToMany(mappedBy = "debate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<EnterUser> enterUserList;

    @Column
    private Long totalPros;

    @Column
    private Long totalCons;

    @OneToMany(mappedBy = "debate")
    private List<Reply> replyList;


    public static Debate create(DebateLinkRequestDto debateLinkRequestDto, User user, CategoryEnum category) {
        // 얘는 홈트 어쩌고 그거 그대로 긁어온건데 뭔지 모르겠네요
        // 여기서 왜인지 모르겠는데 this. 이 안먹힙니다. 그래서 생성자안에 이렇게 넣어둔것 같아요... 고칠 수 있으면 고치고싶네요ㅠ
        // todo: static(전역적으로 사용할 때) 인 경우 this를 못 쓰는걸로 알고 있습니다.
        Debate debate = new Debate();
        debate.user = user;
        debate.roomId = UUID.randomUUID().toString();
        debate.topic = debateLinkRequestDto.getTopic();
        debate.categoryEnum = category;
        debate.prosName = debateLinkRequestDto.getProsName();
        debate.consName = debateLinkRequestDto.getConsName();
        debate.content = debateLinkRequestDto.getContent();
        return debate;
    }

}
