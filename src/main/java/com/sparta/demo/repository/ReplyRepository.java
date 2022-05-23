package com.sparta.demo.repository;

import com.sparta.demo.model.Reply;
//import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
//    List<Reply> findAllByDebate_DebateId(Long debateId);

    @EntityGraph(attributePaths = {"user"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Reply> findByReplyId(Long replyId);

//    List<Reply> findAllByUser_Email(Sort sort, String email);
    List<Reply> findTop60ByUser_Email(Sort sort, String email);
    List<Reply> findTop60ByDebate_DebateId(Sort sort, Long debateId); // 상세보기 페이지에서 사용하는 정렬
}
