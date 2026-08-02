//package com.example.LeetDeCode_Battle_Module.configs;
//
//import com.example.LeetDeCode_Battle_Module.security.PrincipalHandshakeHandler;
//import com.example.LeetDeCode_Battle_Module.security.StompHandshakeInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173")
//                .addInterceptors(new StompHandshakeInterceptor())
//                .setHandshakeHandler(new PrincipalHandshakeHandler())
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        // Messages sent by clients to destinations prefixed "/app" are routed
//        // to @MessageMapping-annotated controller methods (Phase 3).
//        registry.setApplicationDestinationPrefixes("/app");
//
//        // Messages broadcast to "/topic/**" are relayed to all subscribed clients.
//        // e.g. server publishes to /topic/room/{roomCode} -> every player in that
//        // room's browser receives the update.
//        registry.enableSimpleBroker("/topic");
//    }
//}
