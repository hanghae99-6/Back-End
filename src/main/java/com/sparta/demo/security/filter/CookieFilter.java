//package com.sparta.demo.security.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collection;
//
///*
//    sameSite 오류 해결을 위한 필터 클래스
//*/
//@Slf4j
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CookieFilter implements Filter{
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response,
//                         FilterChain chain) throws IOException, ServletException {
//
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        chain.doFilter(request, response);
//        log.info("CookieAttributeFilter");
//        addSameSite(httpServletResponse , "None");
//
//    }
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public void destroy() {
//        // TODO Auto-generated method stub
//    }
//
//    private void addSameSite(HttpServletResponse response, String sameSite) {
//
//        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
//        boolean firstHeader = true;
//        for (String header : headers) { // there can be multiple Set-Cookie attributes
//            if (firstHeader) {
//                response.setHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite));
//                firstHeader = false;
//                continue;
//            }
//            response.addHeader(HttpHeaders.SET_COOKIE, String.format("%s; Secure; %s", header, "SameSite=" + sameSite));
//        }
//
//    }
//
//}
