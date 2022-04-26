package com.sparta.demo.repository;

import com.sparta.demo.model.Debate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateRepository extends JpaRepository<Debate, Long> {
    Debate findByroomId(String roomId);
}
