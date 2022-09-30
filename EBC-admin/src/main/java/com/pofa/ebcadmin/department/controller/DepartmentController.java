package com.pofa.ebcadmin.department.controller;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.department.dto.Department;
import com.pofa.ebcadmin.department.service.DepartmentService;
import com.pofa.ebcadmin.manufacturer.dto.Manufacturer;
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

@Api(tags = "部门")
@Controller
@RestController
@RequestMapping("department")
public class DepartmentController {
    @Autowired
    public DepartmentService departmentService;

    @ApiOperation(value = "添加部门", notes = "",
            httpMethod = "POST")
    @PostMapping("/add")
    public SaResult departmentAdd(Department.AddDTO dto) {

        var code = departmentService.addDepartment(dto);

        String data = switch (code) {
            case 1 -> "添加成功";
            case -100 -> "部门已存在";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "修改部门信息", notes = "",
            httpMethod = "POST")
    @PostMapping("/modify")
    public SaResult editManufacturer(Department.EditDTO dto) {
        var code = departmentService.editDepartment(dto);

        String data = switch (code) {
            case 1 -> "修改成功";
            default -> "未知错误";
        };

        return SaResult.ok("success").setData(data).setCode(code);
    }

    @ApiOperation(value = "获取所有部门信息", notes = "", httpMethod = "POST")
    @PostMapping("/get")
    public SaResult departmentGet(Department.GetDTO dto) {
        var departments = departmentService.getDepartments();
        return SaResult.ok("success").setData(new JSONObject().fluentPut("departments", departments));
    }
}
