package com.example.cumock.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            System.out.println("[WebSocketHandshakeInterceptor] token: " + token);
            if (token != null) {
                attributes.put("token", token);
                System.out.println("[WebSocketHandshakeInterceptor] Token accepted, handshake allowed");
                return true;
            } else {
                System.out.println("[WebSocketHandshakeInterceptor] No token, handshake allowed for debug");
            }
        }
        return true; // Позволяем соединение даже без токена для отладки
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Не требуется действий после рукопожатия
    }
}
