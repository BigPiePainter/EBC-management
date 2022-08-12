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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "订单")
@Controller
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    public OrderService orderService;


    @ApiOperation(value = "文件上传", notes = "",
            httpMethod = "POST")
    @PostMapping("/fileUpload")
    public SaResult fileUpload(MultipartFile file) {
        System.out.println(file);
        orderService.fileProcess(file);
        return SaResult.ok("success");
    }


    @ApiOperation(value = "获取文件处理状态", notes = "",
            httpMethod = "POST")
    @PostMapping("/getFileProcessStates")
    public SaResult getFileProcessStates() {
        return SaResult.ok("success").setData(new JSONObject().fluentPut("fileStates", FileStateManager.getStates()));
    }


}
