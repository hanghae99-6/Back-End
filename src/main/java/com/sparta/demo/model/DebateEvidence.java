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

    @Column(nullable = false)
    private String roomId;

    @Column
    private String evidence;

    @Column
    private String prosCons;

    public DebateEvidence(String roomId, String evidence, String prosCons) {
        this.roomId = roomId;
        this.evidence = evidence;
        this.prosCons = prosCons;
    }
}
