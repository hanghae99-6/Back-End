package com.sparta.demo.repository;

import com.sparta.demo.model.StpMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StpMessageRepository extends JpaRepository<StpMessage, Long> {
    Long findAllByRoomId(String roomId);

}
