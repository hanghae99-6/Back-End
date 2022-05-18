package com.sparta.demo.model;

import com.sparta.demo.enumeration.SideTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OneClickUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oneClickUserId;

    @Column
    private Long oneClickId;

    @Column
    private String userIp;

    @Enumerated(value = EnumType.STRING)
    @Column
    private SideTypeEnum sideTypeEnum;

    public OneClickUser(String userIp, SideTypeEnum sideTypeEnum, Long oneClickId) {
        this.userIp = userIp;
        this.sideTypeEnum = sideTypeEnum;
        this.oneClickId = oneClickId;
    }
}
