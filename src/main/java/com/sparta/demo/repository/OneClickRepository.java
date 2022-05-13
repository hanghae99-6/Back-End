package com.sparta.demo.repository;

import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.OneClickUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OneClickRepository extends JpaRepository<OneClick, Long> {

    Optional<OneClick> findByOneClickTopicAndOneClickUsers(String oneClickTopic, List<OneClickUser> oneClickUsers);
}
