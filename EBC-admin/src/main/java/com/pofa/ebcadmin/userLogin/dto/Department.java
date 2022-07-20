package com.pofa.ebcadmin.userLogin.dto;

import lombok.Data;

import java.math.BigDecimal;

public class Department {

    //public record GetDTO(Long page, Long itemsPerPage) {}

    @Data
    public static class GetDTO {
    }
    @Data
    public static class AddDTO {
        private String name;
        private String note;
    }
}

