package com.pofa.ebcadmin.shop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public class Shop {
    @Data
    public static class GetDTO {
    }

    @Data
    public static class AddDTO {
        private String name;

        private String note;
    }

    @Data
    public static class DeleteDTO {
        private String name;
    }
}
