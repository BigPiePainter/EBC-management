package com.pofa.ebcadmin.order.entity;


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
@TableName("fakeorders_personal")
public class PersonalFakeOrderInfo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Date orderPaymentTime;

    private Date refundEndTime;
}
