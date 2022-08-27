package com.pofa.ebcadmin.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    @Data
    public static class GetDailyReportDTO {
        private Date date;
    }
}
