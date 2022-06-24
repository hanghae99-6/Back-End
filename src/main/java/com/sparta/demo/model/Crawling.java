package com.sparta.demo.model;

import com.sparta.demo.enumeration.CrawlTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Crawling {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String articleUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String date;

    @Column
    private String author;

    @Column
    private String content2;

    @Column
    @Enumerated(value=EnumType.STRING)
    private CrawlTypeEnum type;

    public Crawling(String articleUrl, String title, String imgUrl, String content, String date, CrawlTypeEnum type){
        this.articleUrl = articleUrl;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.date = date;
        this.type = type;
    }

    public Crawling(String articleUrl, String title, String imgUrl, String content, String content2, String date, CrawlTypeEnum type, String author){
        this.articleUrl = articleUrl;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.content2 = content2;
        this.date = date;
        this.type = type;
        this.author = author;
    }

}
