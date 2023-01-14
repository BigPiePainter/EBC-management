package com.pofa.ebcadmin.order.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.orderUtils.FileStateManager;
import com.pofa.ebcadmin.order.service.OrderService;
import com.pofa.ebcadmin.product.dto.Sku;
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

    @ApiOperation(value = "获取未完结退单", notes = "",
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

    @ApiOperation(value = "获取未匹配个人刷单退款", notes = "",
            httpMethod = "POST")
    @PostMapping("/getMismatchPersonalFakeOrders")
    public SaResult getMismatchPersonalFakeOrders(Order.GetPageDTO dto) {
        return SaResult.ok("success").setData(new JSONObject().fluentPut("mismatchPersonalFakeOrders", orderService.getMismatchPersonalFakeOrders(dto)));
    }

    @ApiOperation(value = "删除未匹配团队刷单", notes = "彻底删除未匹配团队刷单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的未匹配团队刷单的ID，用英文逗号隔开", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "1000,1001", required = false),
    })
    @PostMapping("/delete")
    public SaResult deleteFakeOrders(Order.DeleteFakeOrderDTO dto) {
        log.info("deleteFakeOrders TEST");

        var code = orderService.deleteFakeOrderByIds(dto.getIds());

        log.info(String.valueOf(code));

        String data;
        if (code > 0) {
            data = "成功删除" + code + "条团队刷单";
        } else {
            data = switch (code) {
                default -> "未知错误";
            };
        }


        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "删除未匹配个人刷单", notes = "彻底删除未匹配个人刷单", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的未匹配个人刷单的ID，用英文逗号隔开", dataType = "String", paramType = "query", dataTypeClass = String.class, example = "1000,1001", required = false),
    })
    @PostMapping("/delete")
    public SaResult deletePersonalFakeOrders(Order.DeletePersonalFakeOrderDTO dto) {
        log.info("deletePersonalFakeOrders TEST");

        var code = orderService.deletePersonalFakeOrderByIds(dto.getIds());

        log.info(String.valueOf(code));

        String data;
        if (code > 0) {
            data = "成功删除" + code + "条个人刷单";
        } else {
            data = switch (code) {
                default -> "未知错误";
            };
        }
        return SaResult.ok("success").setData(data).setCode(code);
    }

}
