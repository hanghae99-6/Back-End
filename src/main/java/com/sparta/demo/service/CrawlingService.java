package com.sparta.demo.service;

import com.sparta.demo.dto.main.CrawlingDto;
import com.sparta.demo.enumeration.CrawlTypeEnum;
import com.sparta.demo.model.Crawling;
import com.sparta.demo.repository.CrawlingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final CrawlingRepository crawlingRepository;

    LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));  // 현재 날짜
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String todayDate = now.format(formatter);

    @Transactional
    public ResponseEntity<CrawlingDto> getNaverNews() throws IOException {

        int page = 1;
        CrawlingDto naverNewsDto = new CrawlingDto();

        CrawlTypeEnum type = CrawlTypeEnum.NEWS;

        Crawling crawling = crawlingRepository.findByDateAndType(todayDate, type);

        if(crawling == null){
            crawlingRepository.deleteAll();
            while(true){
                int cnt = 0;

                String url = "https://news.naver.com/main/list.naver?mode=LS2D&mid=shm&sid2=249&sid1=102&date="+todayDate+"&page="+page;
                log.info("page: {}", page);

                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.getElementsByAttributeValue("class", "list_body newsflash_body");

//                Element element = elements.get(0);
                Elements photoElements = elements.get(0).getElementsByAttributeValue("class", "photo");
//            System.out.println("photoElements size: "+ photoElements.size());

                for (int i = 0; i < photoElements.size(); i++) {

                    Element article = photoElements.get(i);

                    Elements aElements = article.select("a");
                    Element aElement = aElements.get(0);


                    String articleUrl = aElement.attr("href"); //기사링크

                    Element imgElement = aElement.select("img").get(0);
                    String imgUrl = imgElement.attr("src"); // 사진링크
                    String title = imgElement.attr("alt"); // 기사 제목


                    Document subDoc = Jsoup.connect(articleUrl).get();
                    Element contentElement = subDoc.getElementById("dic_area");
                    String content = contentElement.text(); // 기사내용

                    if (content.contains("토론") || content.contains("토의") || content.contains("회의")){

                        content = content.substring(0,200);

                        Crawling naverNews = new Crawling(articleUrl, title, imgUrl, content, todayDate, type);

                        log.info("crawling info: {},{},{},{}", articleUrl, title, imgUrl, content);
                        crawlingRepository.save(naverNews);

                        naverNewsDto.setCrawling(naverNews);
                        log.info("articleUrl: {}", naverNewsDto.getCrawling().getArticleUrl());
                        log.info("title: {}", naverNewsDto.getCrawling().getTitle());
                        log.info("imgUrl: {}", naverNewsDto.getCrawling().getImgUrl());
                        log.info("content: {}", naverNewsDto.getCrawling().getContent());
                        cnt++; break;
                    }
                }

                if(cnt==0) page++;
                else break;
            }
        } else {
            naverNewsDto.setCrawling(crawlingRepository.findByDateAndType(todayDate, type));
        }

        log.info("return 잘되었나: "+ crawlingRepository.findByDateAndType(todayDate, type));

        return ResponseEntity.ok().body(naverNewsDto);
    }

    @Transactional
    public ResponseEntity<CrawlingDto> getColumn() throws IOException {

        CrawlingDto crawlingDto = new CrawlingDto();

        CrawlTypeEnum type = CrawlTypeEnum.COLUMN;

        Crawling crawlingColumn = crawlingRepository.findByDateAndType(todayDate, type);

        if(crawlingColumn==null){
            String url = "http://www.koreadebate.org/news/articleList.html?sc_sub_section_code=S2N13";

            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.getElementsByAttributeValue("class", "ArtList_Title");

//        Element element = elements.get(0);

            Elements aElements = elements.get(0).select("a");

            String articleUrl = aElements.attr("href"); //기사링크
            articleUrl = "http://www.koreadebate.org/news/" + articleUrl;

            String title = aElements.toString().split("\"")[3];

            Document doc2 = Jsoup.connect(articleUrl).get();
            Elements elements2 = doc2.getElementsByAttributeValue("class", "view_r");

            Elements imgElement = elements2.select("img");
            String imgUrl = imgElement.attr("src");

            String content = elements2.text().substring(0,200);
//            content = content.split("<")[0];

            Crawling debateColumn = new Crawling(articleUrl, title, imgUrl, content, todayDate, type);

            log.info("crawling info: {},{},{},{}", articleUrl, title, imgUrl, content);
            crawlingRepository.save(debateColumn);

            crawlingDto.setCrawling(debateColumn);
            log.info("articleUrl: {}", crawlingDto.getCrawling().getArticleUrl());
            log.info("title: {}", crawlingDto.getCrawling().getTitle());
            log.info("imgUrl: {}", crawlingDto.getCrawling().getImgUrl());
            log.info("content: {}", crawlingDto.getCrawling().getContent());
        }
        else {
            crawlingDto.setCrawling(crawlingRepository.findByDateAndType(todayDate, type));
        }

        return ResponseEntity.ok().body(crawlingDto);
    }

    @Transactional
    public ResponseEntity<CrawlingDto> getMagazine() throws IOException{
        CrawlingDto crawlingDto = new CrawlingDto();

        CrawlTypeEnum type = CrawlTypeEnum.MAGAZINE;

        Crawling crawlingColumn = crawlingRepository.findByDateAndType(todayDate, type);

        if(crawlingColumn==null){
            String url = "https://magazine.cheil.com/category/insight";
            Document doc = Jsoup.connect(url).get();

            Elements elements = doc.getElementsByAttributeValue("class", "container-lg");

            Elements aElements2 = elements.get(0).getElementsByAttributeValue("class","text-dark").get(0).select("a");

            String articleUrl = aElements2.attr("href"); //기사링크

            String title = aElements2.text();

            Document doc2 = Jsoup.connect(articleUrl).get();
            Elements post = doc2.getElementsByAttributeValue("class", "post-content");

            String imgUrl = post.select("img").get(0).attr("src");
            String content = post.text().substring(0,200);

            Crawling magazine = new Crawling(articleUrl, title, imgUrl, content, todayDate, type);

            log.info("crawling info: {},{},{},{}", articleUrl, title, imgUrl, content);
            crawlingRepository.save(magazine);

            crawlingDto.setCrawling(magazine);
            log.info("articleUrl: {}", crawlingDto.getCrawling().getArticleUrl());
            log.info("title: {}", crawlingDto.getCrawling().getTitle());
            log.info("imgUrl: {}", crawlingDto.getCrawling().getImgUrl());
            log.info("content: {}", crawlingDto.getCrawling().getContent());
        }
        else {
            crawlingDto.setCrawling(crawlingRepository.findByDateAndType(todayDate, type));
        }
        return ResponseEntity.ok().body(crawlingDto);
    }
}
