package com.sparta.demo.signal.config;


import com.sparta.demo.signal.socket.SignalHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    /*
    WebSocketHandlerRegistry 에 webSocketHandler 의 구현체를 등록한다.
    등록된 Handler 는 특정 endpoint("/signal")로 handshake 를 완료한 후
    맺어진 connection 을 관리한다.
    */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 해당 endpoint 로 handshake 가 이루어진다.
        registry.addHandler(signalHandler(), "/signal")
                .setAllowedOrigins("*"); // allow all origins
    }

    public WebSocketHandler signalHandler() {
        return new SignalHandler();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192); // 텍스트 메세지 최대 크기 설정
        container.setMaxBinaryMessageBufferSize(8192); // 바이너리 메세지 최대 크기 설정
        return container;
    }
}
