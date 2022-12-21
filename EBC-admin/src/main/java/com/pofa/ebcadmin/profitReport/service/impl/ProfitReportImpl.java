package com.pofa.ebcadmin.profitReport.service.impl;

import com.pofa.ebcadmin.profitReport.dao.ProfitReportDao;
import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;
import com.pofa.ebcadmin.profitReport.service.ProfitReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


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
    public List<ProfitReportInfo> getProfitReport(Date startDate, Date endDate) {
        System.out.println("获取利润报表 - 利润报表！！ ");
        var profitReports = new ArrayList<List<ProfitReportInfo>>();
        var threads = new ArrayList<Thread>();
        var start = new Date();
        if (startDate.after(endDate)) {
            var time = endDate.getTime();
            endDate.setTime(startDate.getTime());
            startDate.setTime(time);
        }
        System.out.println(startDate);
        System.out.println(endDate);

        var calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        System.out.println(!calendar.getTime().after(endDate));
        while (!calendar.getTime().after(endDate)) {
            final var month = monthFormat.format(calendar.getTime());
            final var day = dayFormat.format(calendar.getTime());
            threads.add(new Thread(() -> {
                log.info(month + "     " + day);
                profitReports.add(profitReportDao.calculateDailyReport(month, day));
            }));
            calendar.add(Calendar.DATE, 1);
        }

        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info(String.valueOf(new Date().getTime() - start.getTime()));
        System.out.println("--------获取到了结果");


        start = new Date();
        var profitReport = new HashMap<String, ProfitReportInfo>();
        profitReports.forEach(r -> r.forEach(item -> {
            var id = item.getDepartment() + "," + item.getTeam() + "," + item.getOwner() + "," + item.getProductId();
            if (!profitReport.containsKey(id)) {
                profitReport.put(id, item);
                return;
            }

            var productProfit = profitReport.get(id);
            productProfit.setOrderCount(productProfit.getOrderCount() + item.getOrderCount());
            productProfit.setProductCount(productProfit.getProductCount() + item.getProductCount());
            productProfit.setTotalAmount(productProfit.getTotalAmount().add(item.getTotalAmount()));
            productProfit.setTotalRefundAmount(productProfit.getTotalRefundAmount().add(item.getTotalRefundAmount()));
            productProfit.setTotalRefundWithNoShipAmount(productProfit.getTotalRefundWithNoShipAmount().add(item.getTotalRefundWithNoShipAmount()));
            productProfit.setTotalFakeCount(productProfit.getTotalFakeCount() + item.getTotalFakeCount());
            productProfit.setTotalFakeAmount(productProfit.getTotalFakeAmount().add(item.getTotalFakeAmount()));
            productProfit.setTotalBrokerage(productProfit.getTotalBrokerage().add(item.getTotalBrokerage()));
            productProfit.setTotalPrice(productProfit.getTotalPrice().add(item.getTotalPrice()));
            productProfit.setTotalCost(productProfit.getTotalCost().add(item.getTotalCost()));
            productProfit.setWrongCount(productProfit.getWrongCount() + item.getWrongCount());

        }));
        log.info(String.valueOf(new Date().getTime() - start.getTime()));
        System.out.println("--------整合时间");


        return profitReport.values().stream().toList();
    }

    @Override
    public List<MismatchedSkusInfo> getMismatchedSkus(Date date, Long productId) {
        System.out.println("获取未匹配Sku! ");
        System.out.println(date);
        var mismatchedSkus = profitReportDao.getMismatchedSkus(monthFormat.format(date), dayFormat.format(date), productId);
        System.out.println("--------获取到了结果");
        return mismatchedSkus;
    }

}

