package com.pofa.ebcadmin.profitReport.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.profitReport.service.ProfitReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

@Api(tags = "利润报表")
@Controller
@RestController
@RequestMapping("profitReport")
@Slf4j
public class ProfitReportController {
    @Autowired
    public ProfitReportService profitReportService;

    @ApiOperation(value = "获取利润报表", notes = "根据日期",
            httpMethod = "POST")
    @PostMapping("/getProfitReport")
    public SaResult getDailyReport(Order.GetDailyReportDTO dto) {
        System.out.println("Get ProfitReport TEST");
        var dailyReport = profitReportService.getProfitReport(dto.getDate());
        var dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        return SaResult.ok("success").setData(new JSONObject().fluentPut(dayFormat.format(dto.getDate()), dailyReport));
    }

}
