package com.sparta.demo.repository;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.DebateVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DebateVoteRepository extends JpaRepository<DebateVote, Long> {
    Optional<DebateVote> findByDebate_DebateIdAndIp(Long debateId, String ip);
    Long countAllBySide(SideTypeEnum side);
    Long countAllBySideAndDebate_DebateId(SideTypeEnum side, Long debateId);

    @Query("select d.side from DebateVote d where d.debate.debateId = ?1 and d.ip = ?2")
    SideTypeEnum getSideByDebateIdAndIp(Long debateId, String ip);
}
