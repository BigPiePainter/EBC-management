package com.pofa.ebcadmin.globalSocket.utils;


import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.utils.webSocket.WebSocketHandler;
import com.pofa.ebcadmin.utils.webSocket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;


@Component
@ServerEndpoint("/ws/global")    // 指定websocket 连接的url
@Slf4j
public class GlobalWebSocket extends WebSocket {
    public static String announcement = "";
    public static Date announcementDate = new Date();

    @OnOpen
    @Override
    public void onOpen(Session session) {
        log.info("WebSocket onOpen id:{}", session.getId());
        this.session = session;
        WebSocketHandler.put("announcement", this);
    }

    @OnClose
    @Override
    public void onClose() {
        log.info("WebSocket onClose id:{}", session.getId());
        WebSocketHandler.remove("announcement", this);
    }


    @OnMessage
    @Override
    public void onMessage(String message, Session session) {
        if (message.equals("announcement")) {
            if (!announcement.isBlank()) {
                sendMessage(generateAnnouncementJsonText());
            }
        }
    }

    public static String generateAnnouncementJsonText() {
        var info = new JSONObject().fluentPut("date", announcementDate.getTime()).fluentPut("content", announcement);
        return info.toString();
    }
}





