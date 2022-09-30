package com.pofa.ebcadmin.order.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@Repository
@TableName("fakeorders")
public class FakeOrderInfo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Date requestTime;
    private Date orderPaymentTime;
    private Long productCount;
    private BigDecimal brokerage;
    private String team;
}
