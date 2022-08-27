package com.pofa.ebcadmin.order.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class DailyReportInfo {
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

    private BigDecimal totalFakeAmount; //补单额
    private Long fakeOrderCount; //补单数
    private BigDecimal totalBrokerage; //刷单佣金

    private BigDecimal totalRefundAmount; //退款金额
    private BigDecimal totalRefundWithNoShipAmount; //未发仅退




    private BigDecimal refundWithNoShipCost; //未发退本
    private Long refundWithNoShipCount; //未发数


    private BigDecimal totalPrice; //原售价
    private BigDecimal totalCost; //拿货成本
    private Long wrongCount; //sku错数






}
