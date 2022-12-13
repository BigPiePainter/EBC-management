package com.pofa.ebcadmin.globalSocket.controller;


import cn.dev33.satoken.util.SaResult;
import com.pofa.ebcadmin.globalSocket.dto.GlobalSocket;
import com.pofa.ebcadmin.globalSocket.utils.GlobalWebSocket;
import com.pofa.ebcadmin.utils.webSocket.WebSocketHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "公告")
@Controller
@RestController
@RequestMapping("globalSocket")
@Slf4j
public class GlobalSocketController {

    @ApiOperation(value = "设置公告", notes = "",
            httpMethod = "POST")
    @PostMapping("/setAnnouncement")
    public void setAnnouncement(GlobalSocket.SetAnnouncementDTO dto) {
        log.info("发送全局公告");
        log.info(String.valueOf(dto));

        GlobalWebSocket.announcementDate = new Date();
        GlobalWebSocket.announcement = dto.getContent();

        WebSocketHandler.sendToAll("announcement", GlobalWebSocket.generateAnnouncementJsonText());
    }

    @ApiOperation(value = "获取公告", notes = "",
            httpMethod = "POST")
    @PostMapping("/getAnnouncement")
    public SaResult getAnnouncement() {
        log.info("获取公告");
        return SaResult.ok("success").setData(GlobalWebSocket.generateAnnouncementJsonText());
    }
}
