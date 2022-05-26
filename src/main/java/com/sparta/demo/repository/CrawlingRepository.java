package com.sparta.demo.repository;


import com.sparta.demo.enumeration.CrawlTypeEnum;
import com.sparta.demo.model.Crawling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlingRepository extends JpaRepository<Crawling, Long> {

    Crawling findByDateAndType(String date, CrawlTypeEnum type);
}
