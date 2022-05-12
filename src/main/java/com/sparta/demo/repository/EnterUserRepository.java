package com.sparta.demo.repository;

import com.sparta.demo.model.EnterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnterUserRepository extends JpaRepository<EnterUser, Long> {
    Optional<Integer> countAllByDebate_RoomId(String roomId);
}
