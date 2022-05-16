package com.sparta.demo.repository;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.OneClickUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OneClickUserRepository extends JpaRepository<OneClickUser, Long> {

    List<OneClickUser> findByOneClickIdAndSideTypeEnum(Long oneClickId, SideTypeEnum sideTypeEnum);

    Optional<OneClickUser> findByUserIpAndOneClickId(String userIp, Long oneClickId);

}
