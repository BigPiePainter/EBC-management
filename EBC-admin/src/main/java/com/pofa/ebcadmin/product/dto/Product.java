package com.pofa.ebcadmin.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Product {

    //public record GetDTO(Long page, Long itemsPerPage) {}

    @Data
    public static class GetDTO {
        private Long page;
        private Long itemsPerPage;

        private String match;
    }

    @Data
    public static class AddDTO {
        private Long id;
        private Long department;
        private Long team;
        private Long owner;
        private String shopName;
        private Long firstCategory;
        private String productName;
        private String transportWay;
        private String storehouse;

        private String note;
    }

    @Data
    public static class EditDTO {
        private Long id;
        private Long department;
        private Long team;
        private Long owner;
        private String shopName;
        private Long firstCategory;
        private String productName;
        private String transportWay;
        private String storehouse;

        private Date startTime;

        private String note;
    }

    @Data
    public static class deleteDTO {
        private Long id;
    }
}

