package com.pofa.ebcadmin;

import org.apache.poi.util.IOUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
//@MapperScan("com.pofa.ebcadmin.mapper")
public class EbcAdminApplication {

    public static void main(String[] args) {
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
        SpringApplication.run(EbcAdminApplication.class, args);
    }

}
