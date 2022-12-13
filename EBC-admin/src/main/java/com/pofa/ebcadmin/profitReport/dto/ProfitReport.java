package com.pofa.ebcadmin.profitReport.dto;

import lombok.Data;

import java.util.Date;

public class ProfitReport {
    @Data
    public static class GetDTO {
        private Date date;
    }

}
