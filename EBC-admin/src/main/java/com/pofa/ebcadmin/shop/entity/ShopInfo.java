package com.pofa.ebcadmin.shop.entity;


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
@TableName("shops")
public class ShopInfo {

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private String note;



}
