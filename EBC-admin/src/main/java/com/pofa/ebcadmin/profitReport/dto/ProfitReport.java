package com.pofa.ebcadmin.profitReport.dto;

import lombok.Data;

import java.util.Date;

public class ProfitReport {
    @Data
    public static class GetDTO {
        private Date startDate;
        private Date endDate;
    }

    @Data
    public static class GetMismatchedSkusDTO {
        private Date date;
        private Long productId;
    }

}
