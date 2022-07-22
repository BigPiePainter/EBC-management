package com.pofa.ebcadmin.userLogin.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Data
@Accessors(chain = true)
@Repository
@TableName("users")
public class UserInfo {
    private Long uid;
    private Long creatorId;

    private Long permission;

    private String username;
    private String password;


    private String nick;
    private String note;
}