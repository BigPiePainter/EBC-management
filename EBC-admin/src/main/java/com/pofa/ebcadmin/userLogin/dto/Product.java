package com.pofa.ebcadmin.userLogin.dto;

import lombok.Data;

import java.math.BigDecimal;

public class Product {

    //public record GetDTO(Long page, Long itemsPerPage) {}

    @Data
    public static class GetDTO {
        private Long page;
        private Long itemsPerPage;

        private String department;
        private String groupName;
        private String owner;
        private String shopName;
        private String firstCategory;
        private String transportWay;
        private String manufacturerName;
        private String manufacturerPaymentMethod;

    }
    @Data
    public static class AddDTO {
        private String id;
        private String department;
        private String groupName;
        private String owner;
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
        private String manufacturerName;
        private String manufacturerGroup;
        private String manufacturerPaymentMethod;
        private String manufacturerPaymentName;
        private String manufacturerPaymentId;
        private String manufacturerRecipient;
        private String manufacturerPhone;
        private String manufacturerAddress;
    }
}

