package com.pofa.ebcadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.pofa.ebcadmin.mapper")
public class EbcAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbcAdminApplication.class, args);
    }

}
