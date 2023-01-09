package com.pofa.ebcadmin.mybatisplus;


import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class CustomTableNameHandler implements TableNameHandler {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static ThreadLocal<String> customTableName = new ThreadLocal<>();

    public CustomTableNameHandler() {
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (tableName.equals("orders") || tableName.equals("refundorders") || tableName.equals("fakeorders") || tableName.equals("fakeorders_personal")) {
            var newName = customTableName.get();
            if (null == newName){
                return tableName;
            }
            log.info(tableName + " 发生 TABLE 替换, 替换为 " + newName);
            return newName;
        }
        return tableName;
    }

}