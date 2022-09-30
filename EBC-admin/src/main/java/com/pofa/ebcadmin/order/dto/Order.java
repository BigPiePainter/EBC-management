package com.pofa.ebcadmin.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    @Data
    public static class GetDailyReportDTO {
        private Date date;
    }

    @Data
    public static class DeleteFileStateDTO {
        private String fileName;
    }

    @Data
    public static class GetPageDTO {
        private Long page;
        private Long itemsPerPage;
    }
}
