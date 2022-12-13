package com.pofa.ebcadmin.utils.webSocket;


import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;


@Slf4j
public class WebSocket {
    public Session session;

    @OnOpen
    public void onOpen(Session session) {}

    @OnClose
    public void onClose() {}

    @OnMessage
    public void onMessage(String message, Session session) {}

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket onError");
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("消息发送失败: {}", e.getMessage(), e);
        }
    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            log.error("session 关闭失败: {}", e.getMessage(), e);
        }
    }




}



