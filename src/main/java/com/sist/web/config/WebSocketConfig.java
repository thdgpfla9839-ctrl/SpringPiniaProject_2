package com.sist.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

    	// topic : 전체 메시지를 날릴 때
    	// queue : 개인 메시지를 날릴 때
    	// 이 형태는 내맘대로 바꿀 수 있음 topic => all 이런식으로
        registry.enableSimpleBroker(
                "/topic",
                "/queue"
        );

        // 클라이언트에서 서버를 요청을 할 때
        // /app/chat/public : 전체 채팅
        // /app/chat/private : 1:1 
        // 컨트롤러에서는 /app를 생략하고 읽는다
        registry.setApplicationDestinationPrefixes(
                "/app"
        );

        // /user/queue/chat
        registry.setUserDestinationPrefix(
                "/user"
        );
    }

    // 웹소캣 연결주소를 지정할 때
    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        registry.addEndpoint("/chat-ws")
                 // 모든 사람이 접급이 가능하게 지정
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}