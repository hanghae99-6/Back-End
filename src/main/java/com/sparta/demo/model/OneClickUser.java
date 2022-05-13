package com.sparta.demo.model;

import com.sparta.demo.enumeration.SideTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class OneClickUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oneClickUserId;

    @Column
    private String userIp;

    @Column
    private SideTypeEnum sideTypeEnum;
}
