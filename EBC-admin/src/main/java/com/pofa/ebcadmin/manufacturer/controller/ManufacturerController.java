package com.pofa.ebcadmin.manufacturer.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.nimbusds.jose.JOSEException;
import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;
import com.pofa.ebcadmin.manufacturer.service.ManufacturerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Api(tags = "厂家")
@Controller
@RestController
@RequestMapping("manufacturer")
public class ManufacturerController {
    @Autowired
    public ManufacturerService manufacturerService;


    @ApiOperation(value = "添加厂家信息", notes = "根据商品ID",
            httpMethod = "POST")
    @PostMapping("/add")
    public SaResult manufacturerAdd(Manufacturer.AddDTO dto) {
        //List<ManufacturerInfo> userInfos = manufacturerService.userLogin(user.getUsername(), user.getPassword());
        var code = manufacturerService.addManufacturer(dto);
        return SaResult.ok("success").setData("").setCode(code);
    }

    @ApiOperation(value = "获取厂家信息", notes = "通过商品ID",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品ID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "10000", required = false),
    })
    @PostMapping("/get")
    public SaResult manufacturerGet(Manufacturer.GetDTO dto) {
        var manufacturers = manufacturerService.getManufacturersByProductId(dto.getProductId());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("manufacturers", manufacturers));
    }
}
