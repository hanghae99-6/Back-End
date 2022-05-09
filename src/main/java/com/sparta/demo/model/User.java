package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String nickName; // 카톡 닉네임이 곧 유저네임

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String profileImg;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private Long naverId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Debate> debateList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Reply> replyList;



    @Builder
    public User(String userName, String nickName, String enPassword, String email, String profileImg) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = enPassword;
        this.email = email;
        this.profileImg = profileImg;
    }


    public User update(String nickName, String profileImg){
        this.nickName = nickName;
        this.profileImg = profileImg;
        return this;
    }
}
