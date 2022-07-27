package com.pofa.ebcadmin.manufacturer.dto;

import lombok.Data;

import java.util.Date;

public class Manufacturer {
    @Data
    public static class GetDTO {
        private Long productId;
    }

    @Data
    public static class AddDTO {
        private Long product_id;
        private Date startTime;

        private String manufacturerName;
        private String manufacturerGroup;
        private String manufacturerPaymentMethod;
        private String manufacturerPaymentName;
        private String manufacturerPaymentId;
        private String manufacturerRecipient;
        private String manufacturerPhone;
        private String manufacturerAddress;

        private String note;
    }
}
