package com.sparta.demo.repository;

import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.model.Debate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DebateRepository extends JpaRepository<Debate, Long> {
    Optional<Debate> findByRoomId(String roomId);
//    List<Debate> findAllByOrderByCreatedAtDesc();
//    List<Debate> findAllByCatNameOrderByCreatedAt(String categoryName);
//    List<Debate> findByCatNameOrderByCreatedAtDesc(String categoryName);
//    List<Debate> findAllByCatNameContains(String categoryName);
    Optional<Debate> findByDebateId(Long debateId);


    List<Debate> findAllByCategoryEnumContains(CategoryEnum categoryName);
    List<Debate> findAllByCategoryEnum(CategoryEnum categoryName);

    List<Debate> findAllByOrderByCreatedAtDesc();

    Optional<Debate> findByRoomIdAndProsName(String roomId, String username);

    Optional<Debate> findByRoomIdAndConsName(String roomId, String username);
}
