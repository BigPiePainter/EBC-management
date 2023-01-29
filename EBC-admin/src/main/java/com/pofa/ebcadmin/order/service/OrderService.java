package com.pofa.ebcadmin.order.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderService {

    void fileProcess(MultipartFile file);

    BigDecimal getRealTotalAmount(Date date);

    JSONObject getOrdersByDate(Order.GetOrderDTO dto);

    JSONObject getMismatchRefundOrders(Order.GetPageDTO dto);

    JSONObject getMismatchFakeOrders(Order.GetPageDTO dto);

    JSONObject getMismatchPersonalFakeOrders(Order.GetPageDTO dto);

    int deleteFakeOrderByIds(String ids);

    int deletePersonalFakeOrderByIds(String ids);

}
