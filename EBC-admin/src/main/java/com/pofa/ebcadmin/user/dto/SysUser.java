package com.pofa.ebcadmin.user.dto;

import lombok.Data;

import java.util.Date;

public class SysUser {
    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }

    @Data
    public static class ChangePasswordDTO {
        private String oldPassword;
        private String newPassword;

        private String repeatPassword;
    }



    @Data
    public static class RegistDTO {



        private Integer gender;         //女0，男1
        private String contact;
        private String permission;      //JSON格式的权限设计

        private Date onboardingTime;    //入职日期

        private Long department;

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

        private Date onboardingTime;    //入职日期

        private Long department;

        private String username;
        private String password;

        private String nick;
        private String note;
        private Boolean deprecated;     //是否离职
    }

}
