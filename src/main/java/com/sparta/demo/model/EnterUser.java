package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    public EnterUser(Debate debate, String username) {
        this.debate = debate;
        this.userName = username;
    }

}
