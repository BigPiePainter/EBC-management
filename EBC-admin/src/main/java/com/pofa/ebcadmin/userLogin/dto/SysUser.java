package com.pofa.ebcadmin.userLogin.dto;

import lombok.Data;

public class SysUser {
    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }
}
