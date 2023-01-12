package com.pofa.ebcadmin.user.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("users")
public class UserInfo {
    private Long uid;



    private Integer gender;         //女0，男1
    private String contact;
    private String permission;      //JSON格式的权限设计

    private Long department;

    private String username;
    private String password;

    private Date onboardingTime;

    private String nick;


    @TableField(fill = FieldFill.INSERT)
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deprecated;
}
