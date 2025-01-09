package com.consilium.vcg.configs;

import com.consilium.vcg.interceptors.HttpSessionIdHandshakeInterceptor;
import com.consilium.vcg.interceptors.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");// 這句話表示在topic和user這兩個域上可以向客戶端發訊息。
        config.setUserDestinationPrefix("/user");// 這句話表示給指定使用者傳送一對一的主題字首是"/user"。
        config.setApplicationDestinationPrefixes("/app");// 這句話表示客戶單向伺服器端傳送時的主題上面需要加"/app"作為字首。

    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS().setInterceptors(httpSessionIdHandshakeInterceptor());

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(createUserInterceptor());
    }


    @Bean
    public HttpSessionIdHandshakeInterceptor httpSessionIdHandshakeInterceptor() {
        return new HttpSessionIdHandshakeInterceptor();
    }

    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }
}