package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.demo.dto.debate.DebateInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class EnterUser extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debateId")
    @JsonIgnore
    private Debate debate;

    @Column
    private String userEmail;

    @Column
    private String userImage;

    @Column
    private String type;

    @Column
    private String opinion;

    @OneToMany
    private List<DebateEvidence> evidences;

    public EnterUser(Debate debate, String userEmail) {
        this.debate = debate;
        this.userEmail = userEmail;
    }

    public EnterUser(Debate debate, DebateInfoDto debateInfoDto, String userEmail, List<DebateEvidence> evidences, String userImage) {
        this.debate = debate;
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.type = debateInfoDto.getProsCons();
        this.opinion = debateInfoDto.getOpinion();
        this.evidences = evidences;
    }
}


