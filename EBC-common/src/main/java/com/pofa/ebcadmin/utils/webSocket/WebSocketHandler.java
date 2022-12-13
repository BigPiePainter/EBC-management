package com.pofa.ebcadmin.utils.webSocket;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public
class WebSocketHandler {
    // 用于缓存用户的会话，value之所以是一个集合，是为了保存同一个用户多终端登录的会话
    public static Map<String, Set<WebSocket>> holder = new ConcurrentHashMap<>();
    // 计数器，用于统计当前登录用户会话数
    public static AtomicInteger counter = new AtomicInteger();

    /**
     * 存储session
     * @param category
     * @param session
     */
    public static void put(String category, WebSocket session) {
        Set<WebSocket> sessions = holder.getOrDefault(category, new HashSet<>());
        if (sessions.size() == 0) {
            holder.put(category, sessions);
        }
        sessions.add(session);
        // 计数
        int c = counter.incrementAndGet();

        log.info("用户{}加入,当前在线会话为: {}", category, c);
    }


    public static Set<WebSocket> get(String sid) {
        Set<WebSocket> set = new HashSet<>();
        if (sid.isEmpty()) {
            // sid标识为空
            holder.values().forEach(set::addAll);
        } else {
            // sid不为空
            if (holder.containsKey(sid)) {
                set.addAll(holder.get(sid));
            }
        }
        return set;
    }


    public static void remove(String category, WebSocket socket) {
        Set<WebSocket> sockets = holder.get(category);
        sockets.remove(socket);
        if (sockets.size() == 0) {
            holder.remove(category);
        }
        int c = counter.decrementAndGet();
        log.info("用户{}退出,当前在线会话为: {}", category, c);
    }

    public static void sendToAll(String category, String content) {
        if (holder.containsKey(category)){
            holder.get(category).forEach(session -> {
                session.sendMessage(content);
            });
        }
    }
}