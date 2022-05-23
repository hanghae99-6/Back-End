package com.sparta.demo.repository;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.EnterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnterUserRepository extends JpaRepository<EnterUser, Long> {

    Optional<EnterUser> findBySideAndDebate_RoomId(SideTypeEnum sideTypeEnum, String roomId);

    List<EnterUser> findByDebate_DebateIdOrderBySideDesc(Long debateId);
}
