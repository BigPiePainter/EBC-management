package com.pofa.ebcadmin.userLogin.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;


@Data
@Accessors(chain = true)
@Repository
@TableName("sku")
public class SkuInfo {
    private String product_id;
    private String sku_id;
    private String sku_name;
    private Double price;
    private Double cost;
    private Double start_time;
    private Double end_time;
}