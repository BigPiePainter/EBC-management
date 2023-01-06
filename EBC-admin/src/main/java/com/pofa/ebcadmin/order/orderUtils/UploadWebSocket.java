package com.pofa.ebcadmin.order.orderUtils;


import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.globalSocket.utils.GlobalWebSocket;
import com.pofa.ebcadmin.utils.webSocket.WebSocket;
import com.pofa.ebcadmin.utils.webSocket.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;


@Component
@ServerEndpoint("/ws/upload")    // 指定websocket 连接的url
@Slf4j
public class UploadWebSocket extends WebSocket {

    @OnOpen
    @Override
    public void onOpen(Session session) {
        log.info("Upload WebSocket onOpen id:{}", session.getId());
        this.session = session;
        WebSocketHandler.put("upload", this);
    }

    @OnClose
    @Override
    public void onClose() {
        log.info("WebSocket onClose id:{}", session.getId());
        WebSocketHandler.remove("upload", this);
    }

    public static void sendStateToAll(FileState state, boolean force) {
        if (!force) {
            var now = new Date().getTime();
            if (now - state.getSocketSendTime() < 100) {
                return;
            }
            state.setSocketSendTime(now);
        }
        WebSocketHandler.sendToAll("upload", new JSONObject().fluentPut(state.getFileName(), state).toString());
    }

    public static void sendStateToAll(FileState state) {
        sendStateToAll(state, false);
    }
}





