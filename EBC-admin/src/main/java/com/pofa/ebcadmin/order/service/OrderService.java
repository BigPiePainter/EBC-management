package com.pofa.ebcadmin.order.service;

import com.pofa.ebcadmin.order.dto.Order;
import com.pofa.ebcadmin.order.entity.DailyReportInfo;
import com.pofa.ebcadmin.order.entity.OrderInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface OrderService {

    void fileProcess(MultipartFile file);


    List<DailyReportInfo> getDailyReport(Date date);



}
