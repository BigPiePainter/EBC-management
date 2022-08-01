package com.pofa.ebcadmin.userLogin.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

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
        private String department;
        private String groupName;
        private Long owner;
        private String shopName;
        private String firstCategory;
        private String productName;
        private BigDecimal productDeduction;
        private BigDecimal productInsurance;
        private BigDecimal productFreight;
        private BigDecimal extraRatio;
        private BigDecimal freightToPayment;
        private String transportWay;
        private String storehouse;
    }
}

