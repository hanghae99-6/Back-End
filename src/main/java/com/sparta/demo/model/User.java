package com.sparta.demo.model;

import lombok.*;

import javax.persistence.*;

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

    @Column(nullable = false, unique = true)
    private String nickName; // 카톡 닉네임이 곧 유저네임

    @Column(nullable = false)
    private String password;

    @Column
    private String profileImg;


    public User(String userName, String nickName, String enPassword, String profileImg) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = enPassword;
        this.profileImg = profileImg;
    }
}
