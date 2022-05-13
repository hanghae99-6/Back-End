package com.sparta.demo.model;

import com.sparta.demo.enumeration.SideTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class DebateVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dvId;

    @Column
    private String ip;

    @Column
    private SideTypeEnum side;

    @ManyToOne
    @JoinColumn(name = "debateId")
    private Debate debate;

    public DebateVote(Debate debate, String ip, SideTypeEnum side) {
        this.ip = ip;
        this.debate = debate;
        this.side = side;
    }
}
