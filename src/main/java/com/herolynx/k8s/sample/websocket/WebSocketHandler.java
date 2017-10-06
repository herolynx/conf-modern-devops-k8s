package com.herolynx.k8s.sample.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;

public class WebSocketHandler extends TextWebSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    @Override
    public synchronized void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        session.sendMessage(new TextMessage("\nWeb-socket timer is on!\n"));
        new Thread() {
            @Override
            public synchronized void run() {
                try {
                    while (session.isOpen()) {
                        session.sendMessage(new TextMessage("" + new Date() + "\n"));
                        Thread.currentThread().wait(1000);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
                .start();

    }

}
