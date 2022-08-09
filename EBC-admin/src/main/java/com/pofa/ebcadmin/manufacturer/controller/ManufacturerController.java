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
    public SaResult addManufacturer(Manufacturer.AddDTO dto) {
        //List<ManufacturerInfo> userInfos = manufacturerService.userLogin(user.getUsername(), user.getPassword());
        var code = manufacturerService.addManufacturer(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "修改厂家信息", notes = "根据商品ID",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editManufacturer(Manufacturer.EditDTO dto) {
        //List<ManufacturerInfo> userInfos = manufacturerService.userLogin(user.getUsername(), user.getPassword());
        System.out.println("modify Manufacturer");
        System.out.println(dto);
        var code = manufacturerService.editManufacturer(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取厂家信息", notes = "通过商品ID",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品ID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "10000", required = false),
    })
    @PostMapping("/get")
    public SaResult getManufacturer(Manufacturer.GetDTO dto) {
        System.out.println("getManufacturers TEST");
        var manufacturers = manufacturerService.getManufacturersByProductId(dto.getProductId());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("manufacturers", manufacturers));
    }

    @ApiOperation(value = "删除厂家信息", notes = "通过Uid",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "厂家UID", dataType = "Long", paramType = "query", dataTypeClass = Long.class, example = "10000", required = false),
    })
    @PostMapping("/delete")
    public SaResult deleteManufacturer(Manufacturer.DeleteDTO dto) {
        System.out.println("deleteManufacturers TEST");
        var code = manufacturerService.deprecateManufacturersByUid(dto.getUid());
        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };
        return SaResult.ok("success").setData(data).setCode(code);
    }



}
