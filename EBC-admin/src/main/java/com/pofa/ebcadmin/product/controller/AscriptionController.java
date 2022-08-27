package com.pofa.ebcadmin.product.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.product.dto.Ascription;
import com.pofa.ebcadmin.product.dto.Sku;
import com.pofa.ebcadmin.product.service.AscriptionService;
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

import java.util.Date;

@Api(tags = "商品归属")
@Controller
@RestController
@RequestMapping("ascription")
@Slf4j
public class AscriptionController {


    @Autowired
    public AscriptionService ascriptionService;


    @ApiOperation(value = "读取商品归属信息", notes = "根据商品ID", httpMethod = "POST")
    @PostMapping("/get")
    public SaResult getAscriptions(Ascription.getDTO dto) {
        log.info("getAscriptions TEST");
        var ascriptions = ascriptionService.getAscriptionInfosByProductId(dto.getProductId());
        return SaResult.ok("success").setData(new JSONObject().fluentPut("ascriptions", ascriptions));
    }


    @ApiOperation(value = "删除商品归属信息", notes = "彻底删除，慎用", httpMethod = "POST")
    @PostMapping("/delete")
    public SaResult deleteAscription(Ascription.deleteDTO dto) {
        log.info("deleteAscription TEST");
        log.info(String.valueOf(new Date().getTime()));

        var code = ascriptionService.deleteAscriptionInfoByUid(dto.getUid());
        log.info(String.valueOf(new Date().getTime()));


        String data = switch (code) {
            case 1 -> "删除成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }
}
