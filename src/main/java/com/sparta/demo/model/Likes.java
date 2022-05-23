package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likesId;

    @Column(columnDefinition = "integer default 0")
    private int status;

    @Column
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replyId")
    @NotNull
    @JsonIgnore
    private Reply reply;

    public Likes(ReplyLikesRequestDto replyLikesRequestDto, String ip, Reply reply) {
        this.status = replyLikesRequestDto.getStatus();
        this.ip = ip;
        this.reply = reply;
    }
}
