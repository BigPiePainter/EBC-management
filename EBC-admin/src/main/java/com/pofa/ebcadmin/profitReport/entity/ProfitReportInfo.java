package com.pofa.ebcadmin.profitReport.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProfitReportInfo {

    @TableField(exist = false)
    private String id;

    private Long productId;

    private String shopName;

    private Long firstCategory;

    private BigDecimal deduction;
    private BigDecimal insurance;

    private String productName;

    private BigDecimal freight;
    private BigDecimal freightToPayment;

    private Long department;
    private Long team;
    private Long owner;


    private Long orderCount;
    private Long productCount; //售卖数
    private BigDecimal totalAmount;

    private Long totalFakeCount;
    private BigDecimal totalFakeAmount;
    private Long totalPersonalFakeCount;
    private BigDecimal totalPersonalFakeAmount;
    private Long totalPersonalFakeEnablingCount;
    private BigDecimal totalPersonalFakeEnablingAmount;


    private BigDecimal totalBrokerage; //刷单佣金

    private BigDecimal totalRefundAmount; //退款金额


    private Long totalRefundWithNoShipCount; //未发数

    private BigDecimal totalRefundWithNoShipAmount; //未发仅退

    private BigDecimal refundWithNoShipCost; //未发退本



    private BigDecimal totalPrice; //原售价
    private BigDecimal totalCost; //拿货成本
    private Long wrongCount; //sku错数


}
