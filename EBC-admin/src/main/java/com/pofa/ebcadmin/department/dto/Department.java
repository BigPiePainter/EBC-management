package com.pofa.ebcadmin.department.dto;

import lombok.Data;

public class Department {

    //public record GetDTO(Long page, Long itemsPerPage) {}

    @Data
    public static class GetDTO {
    }
    @Data
    public static class AddDTO {
        private String name;
        private String admin;
        private String note;
    }

    @Data
    public static class EditDTO {
        private Long uid;
        private String name;
        private String admin;
        private String note;
    }
}

