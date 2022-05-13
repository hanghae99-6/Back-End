package com.sparta.demo.repository;

import com.sparta.demo.model.OneClickUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OneClickUserRepository extends JpaRepository<OneClickUser, Long> {
}
