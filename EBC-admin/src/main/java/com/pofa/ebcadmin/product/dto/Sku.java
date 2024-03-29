package com.pofa.ebcadmin.product.dto;

import lombok.Data;

public class Sku {
    @Data
    public static class getDTO {
        private Long productId;
    }

    @Data
    public static class addDTO {
        private Long productId;
        private String data;   //json
    }

    @Data
    public static class deleteDTO {
        private String uids;
    }
}
