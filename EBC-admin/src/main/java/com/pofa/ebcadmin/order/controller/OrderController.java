package com.pofa.ebcadmin.order.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.orderUtils.FileStateManager;
import com.pofa.ebcadmin.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

@Api(tags = "订单")
@Controller
@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    public OrderService orderService;


    @ApiOperation(value = "文件上传", notes = "",
            httpMethod = "POST")
    @PostMapping("/fileUpload")
    public SaResult fileUpload(MultipartFile file) {
        log.info(String.valueOf(file));
        orderService.fileProcess(file);
        log.info("返回上传结果");
        return SaResult.ok("success");
    }


    @ApiOperation(value = "获取文件处理状态", notes = "",
            httpMethod = "POST")
    @PostMapping("/getFileProcessStates")
    public SaResult getFileProcessStates() {
        return SaResult.ok("success").setData(new JSONObject().fluentPut("fileStates", FileStateManager.getStates()));
    }


    @ApiOperation(value = "删除某个文件处理状态", notes = "",
            httpMethod = "POST")
    @PostMapping("/deleteFileProcessState")
    public SaResult deleteFileProcessState(Order.DeleteFileStateDTO dto) {
        for (var fileName:dto.getFileName().split("!@#!#@!@#")) {
            FileStateManager.removeFile(fileName);
        }
        return SaResult.ok("success");
    }

    @ApiOperation(value = "获取未匹配退单", notes = "",
            httpMethod = "POST")
    @PostMapping("/getMismatchRefundOrders")
    public SaResult getMismatchRefundOrders(Order.GetPageDTO dto) {
        return SaResult.ok("success").setData(new JSONObject().fluentPut("mismatchRefundOrders", orderService.getMismatchRefundOrders(dto)));
    }

    @ApiOperation(value = "获取未匹配刷单", notes = "",
            httpMethod = "POST")
    @PostMapping("/getMismatchFakeOrders")
    public SaResult getMismatchFakeOrders(Order.GetPageDTO dto) {
        return SaResult.ok("success").setData(new JSONObject().fluentPut("mismatchFakeOrders", orderService.getMismatchFakeOrders(dto)));
    }

    @ApiOperation(value = "获取利润报表", notes = "根据日期",
            httpMethod = "POST")
    @PostMapping("/getDailyReport")
    public SaResult getDailyReport(Order.GetDailyReportDTO dto) {
        System.out.println("Get DailyReport TEST");
        var dailyReport = orderService.getDailyReport(dto.getDate());
        var dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        return SaResult.ok("success").setData(new JSONObject().fluentPut(dayFormat.format(dto.getDate()), dailyReport));
    }

}
