package com.sparta.demo.repository;

import com.sparta.demo.model.Debate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DebateRepository extends JpaRepository<Debate, Long> {
    Optional<Debate> findByRoomId(String roomId);
    List<Debate> findAllByOrderByCreatedAtDesc();
//    List<Debate> findAllByCatNameOrderByCreatedAt(String categoryName);
//    List<Debate> findByCatNameOrderByCreatedAtDesc(String categoryName);
    List<Debate> findAllByCatNameContains(String categoryName);
    Debate findByDebateId(Long debateId);
}
