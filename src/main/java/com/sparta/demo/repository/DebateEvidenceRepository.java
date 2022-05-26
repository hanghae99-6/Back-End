package com.sparta.demo.repository;

import com.sparta.demo.model.DebateEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebateEvidenceRepository extends JpaRepository<DebateEvidence, Long> {
}
