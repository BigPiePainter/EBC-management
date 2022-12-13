package com.pofa.ebcadmin.profitReport.service.impl;

import com.pofa.ebcadmin.profitReport.dao.ProfitReportDao;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;
import com.pofa.ebcadmin.profitReport.service.ProfitReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Slf4j
public class ProfitReportImpl implements ProfitReportService {

    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public ProfitReportDao profitReportDao;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<ProfitReportInfo> getProfitReport(Date date) {
        System.out.println("获取利润报表 - 单日利润报表! ");
        System.out.println(date);

        var dailyReportInfo = profitReportDao.calculateDailyReport(monthFormat.format(date), dayFormat.format(date));
        System.out.println("--------结果");
        //System.out.println(dailyReportInfo);

        return dailyReportInfo;
    }

}

