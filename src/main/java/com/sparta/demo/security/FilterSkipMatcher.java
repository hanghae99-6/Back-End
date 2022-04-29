package com.sparta.demo.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

// todo: JwtAuthFilter를 거치는 모든 API 중 로그인 전에 허용되어야 하는 API에 대해 예외 처리를 해준다.(로그인, 회원가입 페이지, css ...)
public class FilterSkipMatcher  implements RequestMatcher {

    private final OrRequestMatcher orRequestMatcher;
    private final RequestMatcher   processingMatcher;

    public FilterSkipMatcher(
            List<String> pathToSkip,
            String processingPath
    ) {
        this.orRequestMatcher = new OrRequestMatcher(pathToSkip
                .stream()
                .map(this :: httpPath)
                .collect(Collectors.toList()));
        this.processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    private AntPathRequestMatcher httpPath(String skipPath) {
        String[] splitStr = skipPath.split(",");

        /*
         * 배열 [1] httpMethod 방식 post get 인지 구분
         * 배열 [0] 제외하는 url
         * */
        return new AntPathRequestMatcher(
                splitStr[1],
                splitStr[0]
        );
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !orRequestMatcher.matches(request) && processingMatcher.matches(request);
    }
}
