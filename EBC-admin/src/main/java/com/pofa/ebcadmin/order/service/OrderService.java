package com.pofa.ebcadmin.order.service;

import com.alibaba.fastjson2.JSONObject;
import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.entity.DailyReportInfo;
import com.pofa.ebcadmin.order.entity.FakeOrderInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import com.pofa.ebcadmin.order.entity.RefundOrderInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface OrderService {

    void fileProcess(MultipartFile file);


    List<DailyReportInfo> getDailyReport(Date date);


    JSONObject getMismatchRefundOrders(Order.GetPageDTO dto);

    JSONObject getMismatchFakeOrders(Order.GetPageDTO dto);


}
