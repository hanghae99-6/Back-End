package com.sparta.demo.model;

import com.sparta.demo.enumeration.CrawlTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
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

//    @Column
//    private int crawlingType; // 0: NaverNews, 1: 한국디베이트신문

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

    public Crawling(String articleUrl, String title, String imgUrl, String content, CrawlTypeEnum type){
        this.articleUrl = articleUrl;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.type = type;
    }

}
