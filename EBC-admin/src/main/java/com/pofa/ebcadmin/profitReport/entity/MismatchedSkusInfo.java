package com.pofa.ebcadmin.profitReport.entity;


import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MismatchedSkusInfo {
    private String skuName;

    private Long productCount;
    private BigDecimal totalAmount;
}
