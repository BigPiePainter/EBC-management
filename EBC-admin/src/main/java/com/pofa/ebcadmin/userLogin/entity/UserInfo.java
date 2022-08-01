package com.pofa.ebcadmin.userLogin.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("users")
public class UserInfo {
    private Long uid;
    private Long creatorId;


    private Integer gender;         //女0，男1
    private String contact;
    private String permission;      //JSON格式的权限设计

    private String username;
    private String password;

    private String nick;
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;
}
