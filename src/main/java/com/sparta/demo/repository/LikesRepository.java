package com.sparta.demo.repository;

import com.sparta.demo.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByReply_ReplyIdAndIp(Long replyId, String ip);
    Long countAllByStatus(int status);
}
