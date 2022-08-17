package com.pofa.ebcadmin.product.dto;

import lombok.Data;

public class Ascription {
    @Data
    public static class getDTO {
        private Long productId;
    }

    @Data
    public static class deleteDTO {
        private Long uid;
    }
}
