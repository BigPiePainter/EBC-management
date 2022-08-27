package com.pofa.ebcadmin.order.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("refundorders")
public class RefundOrderInfo {
    private Long id;

    private Long orderId;
    private Date orderPaymentTime;
    private Date orderSetupTime;
    private BigDecimal orderAmount;

    private Integer refundType;
    private BigDecimal refundAmount;
    private Date refundSetupTime;
    private Integer refundStatus;
    private String refundReason;
    private Date refundEndTime;

    private Long productId;
    private String productTitle;

    private Boolean expressStatus;
    private String expressInfo;
    private String expressNumber;
    private String expressCompany;
    private Boolean needReturn;
    private String customerServiceStatus;
    private String sellerAddress;
    private String operator;
    private String sellerNote;
}
