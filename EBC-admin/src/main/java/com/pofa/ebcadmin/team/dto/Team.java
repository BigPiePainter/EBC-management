package com.pofa.ebcadmin.team.dto;

import lombok.Data;

public class Team {

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

