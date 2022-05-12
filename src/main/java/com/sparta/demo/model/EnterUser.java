package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.demo.dto.debate.DebateInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
//    @NotNull
    @JsonBackReference
    private Debate debate;

    @Column
    private String userName;

    @Column
    private String type;

    @Column
    private String basis;

    public EnterUser(Debate debate, String username) {
        this.debate = debate;
        this.userName = username;
    }

    public EnterUser(Debate debate, DebateInfoDto debateInfoDto, String userName) {
        this.debate = debate;
        this.userName = userName;
        this.type = debateInfoDto.getType();
        this.basis = debateInfoDto.getBasis();
    }
}
