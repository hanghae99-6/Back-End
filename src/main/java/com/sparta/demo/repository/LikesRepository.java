package com.sparta.demo.repository;

import com.sparta.demo.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByReply_ReplyIdAndIp(Long replyId, String ip);
    Long countAllByReply_ReplyIdAndStatus(Long replyId, int status);

    // Like entity에 있는 status(만)를 가져오는데, 조건 1: replyId, 조건 2: ip
    @Query("select l.status from Likes l where l.reply.replyId = ?1 and l.ip = ?2")
    Integer getStatusByReplyIdAndIp(Long replyId, String ip);
}
