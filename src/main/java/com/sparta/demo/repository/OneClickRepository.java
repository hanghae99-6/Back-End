package com.sparta.demo.repository;

import com.sparta.demo.model.OneClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneClickRepository extends JpaRepository<OneClick, Long> {
}
