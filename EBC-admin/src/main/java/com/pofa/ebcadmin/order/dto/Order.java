package com.pofa.ebcadmin.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    @Data
    public static class GetDTO {
        private Long productId;
    }

    @Data
    public static class AddDTO {
        private Long productId;
        private Date startTime;

        private String manufacturerName;
        private String manufacturerGroup;
        private String manufacturerPaymentMethod;
        private String manufacturerPaymentName;
        private String manufacturerPaymentId;
        private String manufacturerRecipient;
        private String manufacturerPhone;
        private String manufacturerAddress;

        private BigDecimal freight;
        private BigDecimal extraRatio;
        private BigDecimal freightToPayment;

        private String note;
    }

    @Data
    public static class EditDTO {
        private Long uid;

        private Long productId;
        private Date startTime;

        private String manufacturerName;
        private String manufacturerGroup;
        private String manufacturerPaymentMethod;
        private String manufacturerPaymentName;
        private String manufacturerPaymentId;
        private String manufacturerRecipient;
        private String manufacturerPhone;
        private String manufacturerAddress;

        private BigDecimal freight;
        private BigDecimal extraRatio;
        private BigDecimal freightToPayment;

        private String note;
    }

    @Data
    public static class DeleteDTO {
        private Long uid;
    }
}
