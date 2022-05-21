package com.sparta.demo.repository;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.EnterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnterUserRepository extends JpaRepository<EnterUser, Long> {
    Optional<Integer> countAllByDebate_RoomId(String roomId);
    List<EnterUser> findAllByDebate_DebateId(Long debateId);

    Optional<EnterUser> findBySideAndDebate_RoomId(SideTypeEnum sideTypeEnum, String roomId);
}
