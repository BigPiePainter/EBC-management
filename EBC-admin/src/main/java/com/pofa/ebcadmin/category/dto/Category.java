package com.pofa.ebcadmin.category.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Category {
    @Data
    public static class GetDTO {
    }

    @Data
    public static class AddDTO {
        private String name;

        private String note;
    }

    @Data
    public static class EditDTO {
        private Long uid;

        private String name;

        private String note;
    }

    @Data
    public static class GetHistoryDTO {
    }

    @Data
    public static class AddHistoryDTO {
        private Long categoryId;

        private BigDecimal deduction;
        private BigDecimal insurance;

        private Date startTime;

        private String note;
    }

    @Data
    public static class EditHistoryDTO {
        private Long uid;

        private Long categoryId;

        private BigDecimal deduction;
        private BigDecimal insurance;

        private Date startTime;

        private String note;
    }

}
