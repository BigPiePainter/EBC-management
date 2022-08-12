package com.pofa.ebcadmin.product.entity;


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
@TableName("ascriptions")
public class AscriptionInfo {

    private Long uid;
    private Long product;

    private Long owner;
    private Long department;
    private Long team;


    private Date startTime;

    @TableField(fill = FieldFill.INSERT)
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deprecated;
}