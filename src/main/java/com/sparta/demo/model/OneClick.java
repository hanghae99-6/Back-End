package com.sparta.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OneClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oneClickId;

    @Column
    private String userIp;

    @Column(nullable = false)
    private String oneClickTopic;

    @Column
    private String side;

    @Column
    private int agreeNum;

    @Column
    private int oppoNum;


}
