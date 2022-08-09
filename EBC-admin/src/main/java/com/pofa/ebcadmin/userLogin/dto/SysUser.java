package com.pofa.ebcadmin.userLogin.dto;

import lombok.Data;

public class SysUser {
    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }

    @Data
    public static class RegistDTO {

        private Long creatorId;

        private Integer gender;         //女0，男1
        private String contact;
        private String permission;      //JSON格式的权限设计

        private String username;
        private String password;

        private String nick;
        private String note;
    }

    @Data
    public static class EditDTO {
        private Long uid;

        private Long creatorId;

        private Integer gender;         //女0，男1
        private String contact;
        private String permission;      //JSON格式的权限设计

        private String username;
        private String password;

        private String nick;
        private String note;
    }

}
