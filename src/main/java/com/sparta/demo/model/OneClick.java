package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class OneClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oneClickId;

    @Column(nullable = false)
    private String oneClickTopic;

    @Setter
    @Column
    private int agreeNum;

    @Setter
    @Column
    private int oppoNum;

    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    private List<OneClickUser> oneClickUsers;

    // TODO : Builder 객체로 들어오면 위 멤버변수를 각각 빌더 메소드 값으로 바꾼 OneClick 객체를 생성
    private OneClick(Builder builder) {
        this.oneClickTopic = builder.oneClickTopic;
        this.agreeNum = builder.agreeNum;
        this.oppoNum = builder.oppoNum;
    }

    // 정적 클래스인 Builder
    public static class Builder {
        private final String oneClickTopic;
        private int agreeNum;
        private int oppoNum;
        // 필수적인 변수는 생성자로 값을 넣는다
        public Builder(String oneClickTopic) {
            this.oneClickTopic = oneClickTopic;
        }
        // 멤버 변수별 메소드 - Builder 객체를 리턴함
        public Builder agreeNum(int agreeNum) {
            this.agreeNum = agreeNum;
            return this;
        }

        public Builder oppoNum(int oppoNum) {
            this.oppoNum = oppoNum;
            return this;
        }

        public OneClick build() {
            return new OneClick(this);
        }

    }
}
