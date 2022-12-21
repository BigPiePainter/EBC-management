package com.pofa.ebcadmin.profitReport.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.profitReport.dto.ProfitReport;
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

    @ApiOperation(value = "获取完整利润报表", notes = "根据日期",
            httpMethod = "POST")
    @PostMapping("/getProfitReport")
    public SaResult getProfitReport(ProfitReport.GetDTO dto) {
        System.out.println("Get ProfitReport TEST");
        if (Math.abs(dto.getStartDate().getTime() - dto.getEndDate().getTime()) / 86400000 > 31){
            return SaResult.ok("success").setData("选择的日期不能超过一个月").setCode(-1);
        }

        var profitReport = profitReportService.getProfitReport(dto.getStartDate(), dto.getEndDate());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("profitReport", profitReport));
    }



    @ApiOperation(value = "获取某个商品未匹配的SKU", notes = "根据日期",
            httpMethod = "POST")
    @PostMapping("/getMismatchedSkus")
    public SaResult getMismatchedSkus(ProfitReport.GetMismatchedSkusDTO dto) {
        System.out.println("Get MismatchedSkus TEST");
        var mismatchedSkus = profitReportService.getMismatchedSkus(dto.getDate(), dto.getProductId());
        var dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        return SaResult.ok("success").setData(new JSONObject().fluentPut("mismatchedSkus", mismatchedSkus));
    }






}
