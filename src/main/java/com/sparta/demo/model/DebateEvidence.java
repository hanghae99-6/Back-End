package com.sparta.demo.model;

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

    public DebateEvidence(String evidence) {
        this.evidence = evidence;
    }
}
