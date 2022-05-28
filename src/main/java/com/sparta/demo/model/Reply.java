package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.demo.dto.reply.ReplyRequestDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DynamicInsert
public class Reply extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String reply;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debateId")
    @JsonBackReference
    private Debate debate;

    @Column
    @ColumnDefault("0")
    private Long badCnt;

    @Column
    @ColumnDefault("0")
    private Long likesCnt;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Likes> likesList;

    @Column
    @Enumerated(value=EnumType.STRING)
    private SideTypeEnum side;


    public Reply(String reply, Debate debate, User user, SideTypeEnum side) {
        this.reply = reply;
        this.debate = debate;
        this.user = user;
        this.side = side;
    }

    public void updateReply(ReplyRequestDto replyRequestDto){
        this.reply = replyRequestDto.getReply();
    }
}

