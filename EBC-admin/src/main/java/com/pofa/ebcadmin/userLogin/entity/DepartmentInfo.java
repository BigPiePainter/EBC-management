package com.pofa.ebcadmin.userLogin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Data
@Accessors(chain = true)
@Repository
@TableName("departments")
public class DepartmentInfo {
    private Long uid;

    private String name;
    private Date createTime;
    private Date modifyTime;
    private String note;
}