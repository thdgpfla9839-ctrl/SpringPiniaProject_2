package com.sist.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
/*
 *    interface 
 *      => 모든 메소드 추상 메소드 
 *      => 인터페이스는 고정을 한다 
 *         ------------------
 *         | 유지보수가 어렵다 
 *         | 구현된 메소드 추가 => default : 1.8
 *    interface A
 *    {
 *       public void disp(); => 반드시 구현 
 *       public default void display(){}
 *       public static void d(){}
 *    }
 */
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// Endpoint => 서버연결 어떤 URL을 사용할지 
		registry.addEndpoint("/chat-ws")
		        .setAllowedOriginPatterns("*")
		        .withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 서버 => 클라이언트 
		registry.enableSimpleBroker(
		   "/topic",
		   "/queue"
		);
		// 클라이언트 => 서버 
		registry.setApplicationDestinationPrefixes(
			 "/app"
		);
		// 1:1 메세지 
		registry.setUserDestinationPrefix("/user");
	}
   
}
