package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class DebateEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debateEvidenceId;

    @Column
    private String evidence;

    @ManyToOne
    @JoinColumn(name = "enterUserId")
    @JsonIgnore
    private EnterUser enterUser;

    public DebateEvidence(String evidence, EnterUser enterUser) {
        this.evidence = evidence;
        this.enterUser = enterUser;
    }
}
