package com.sparta.demo.repository;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.DebateVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DebateVoteRepository extends JpaRepository<DebateVote, Long> {
    Optional<DebateVote> findByDebate_DebateIdAndIp(Long debateId, String ip);
    Long countAllBySide(SideTypeEnum side);
    Long countAllBySideAndDebate_DebateId(SideTypeEnum side, Long debateId);
}
