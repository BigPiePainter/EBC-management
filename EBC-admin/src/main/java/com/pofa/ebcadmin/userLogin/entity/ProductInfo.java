package com.pofa.ebcadmin.userLogin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Accessors(chain = true)
@Repository
@TableName("products")
public class ProductInfo {
    private Long id;
    private String department;
    private String groupName;
    private Long owner;
    private String shopName;
    private String firstCategory;

    private String productName;
    private BigDecimal productDeduction;
    private BigDecimal productInsurance;
    private BigDecimal productFreight;

    private BigDecimal extraRatio;
    private BigDecimal freightToPayment;
    private String transportWay;
    private String storehouse;


    private Date createTime;
    private Date modifyTime;
}