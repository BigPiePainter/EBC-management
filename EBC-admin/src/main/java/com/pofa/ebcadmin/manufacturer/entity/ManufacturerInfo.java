package com.pofa.ebcadmin.manufacturer.entity;


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
@TableName("manufacturers")
public class ManufacturerInfo {
    private Long uid;
    private Long product_id;

    private String manufacturerName;
    private String manufacturerGroup;

    private String manufacturerPaymentMethod;
    private String manufacturerPaymentName;
    private String manufacturerPaymentId;

    private String manufacturerRecipient;
    private String manufacturerPhone;
    private String manufacturerAddress;

    private Date startTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deprecated;

    @TableField(fill = FieldFill.INSERT)
    private String note;
}
