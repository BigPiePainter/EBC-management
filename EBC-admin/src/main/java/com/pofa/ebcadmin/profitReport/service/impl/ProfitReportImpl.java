package com.pofa.ebcadmin.profitReport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pofa.ebcadmin.department.dao.DepartmentDao;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import com.pofa.ebcadmin.mybatisplus.CustomQueryWrapper;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import com.pofa.ebcadmin.profitReport.dao.ProfitReportDao;
import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;
import com.pofa.ebcadmin.profitReport.service.ProfitReportService;
import com.pofa.ebcadmin.team.dao.TeamDao;
import com.pofa.ebcadmin.team.entity.TeamInfo;
import com.pofa.ebcadmin.user.entity.UserInfo;
import com.pofa.ebcadmin.user.enums.UserPermissionEnum;
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


    @Autowired
    public DepartmentDao departmentDao;

    @Autowired
    public TeamDao teamDao;

    @Autowired
    public ProductDao productDao;

    @Override
    public List<ProfitReportInfo> getProfitReportByUser(Date startDate, Date endDate, UserInfo user) {

        var wrapper = new QueryWrapper<ProductInfo>();


        var permission = false;

        if (user.getUid() == 1L) {
            permission = true;
        } else if (user.getPermissionJSON().containsKey(UserPermissionEnum.PROFIT_REPORT_MANAGEMENT.getKey())) {
            if (user.getPermissionJSON().getJSONObject(UserPermissionEnum.PROFIT_REPORT_MANAGEMENT.getKey()).containsKey(UserPermissionEnum.PROFIT_REPORT_MANAGEMENT.SHOW_FULL_PROFIT_REPORT.getKey())) {
                if (user.getPermissionJSON().getJSONObject(UserPermissionEnum.PROFIT_REPORT_MANAGEMENT.getKey()).getBoolean(UserPermissionEnum.PROFIT_REPORT_MANAGEMENT.SHOW_FULL_PROFIT_REPORT.getKey())) {
                    permission = true;
                }
            }
        }

        if (!permission) {
            var departments = departmentDao.selectList(new QueryWrapper<DepartmentInfo>().select("uid", "admin"));
            var teams = teamDao.selectList(new QueryWrapper<TeamInfo>().select("uid", "admin"));
//            System.out.println(departments);
//            System.out.println(teams);

            var departmentIds = new ArrayList<Long>();
            departments.forEach(departmentInfo -> {
                if (departmentInfo.getAdmin().isEmpty()) return;
                if (List.of(departmentInfo.getAdmin().split(",")).contains(user.getUid().toString())) {
                    departmentIds.add(departmentInfo.getUid());
                }
            });

            var teamIds = new ArrayList<Long>();
            teams.forEach(teamInfo -> {
                if (teamInfo.getAdmin().isEmpty()) return;
                if (List.of(teamInfo.getAdmin().split(",")).contains(user.getUid().toString())) {
                    teamIds.add(teamInfo.getUid());
                }
            });

            if (!departmentIds.isEmpty() && !teamIds.isEmpty()) {
                log.info("是部长, 是组长");
                wrapper.and(i -> i.in("department", departmentIds).or().in("team", teamIds).or().in("owner", user.getUid()));
            } else if (!departmentIds.isEmpty()) {
                log.info("是部长");
                wrapper.and(i -> i.in("department", departmentIds).or().in("owner", user.getUid()));
            } else if (!teamIds.isEmpty()) {
                log.info("是组长");
                wrapper.and(i -> i.in("team", teamIds).or().in("owner", user.getUid()));
            } else {
                log.info("是普通运营");
                wrapper.in("owner", user.getUid());
            }
            var productInfoList = productDao.selectList(wrapper.select("id"));
            System.out.println("权限内商品数量：" + productInfoList.size());
            return _getProfitReport(startDate, endDate, (CustomQueryWrapper<ProductInfo>) new CustomQueryWrapper<ProductInfo>().in("product_id", productInfoList.stream().map(ProductInfo::getId).toList()));
        } else {
            return getProfitReport(startDate, endDate);
        }
    }

    @Override
    public List<ProfitReportInfo> getProfitReport(Date startDate, Date endDate) {
        return _getProfitReport(startDate, endDate, new CustomQueryWrapper<>());
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<ProfitReportInfo> _getProfitReport(Date startDate, Date endDate, CustomQueryWrapper<ProductInfo> productFilterWrapper) {

        System.out.println("获取利润报表 - 利润报表！！ ");

        System.out.println(productFilterWrapper);
        System.out.println(productFilterWrapper.getCustomSqlSegment());
        System.out.println(productFilterWrapper.getSqlSegment());
        System.out.println("---");


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
                profitReports.add(profitReportDao.calculateDailyReport(month, day, productFilterWrapper));
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
            productProfit.setTotalRefundWithNoShipCount(productProfit.getTotalRefundWithNoShipCount() + item.getTotalRefundWithNoShipCount());
            productProfit.setTotalRefundWithNoShipAmount(productProfit.getTotalRefundWithNoShipAmount().add(item.getTotalRefundWithNoShipAmount()));

            productProfit.setTotalFakeCount(productProfit.getTotalFakeCount() + item.getTotalFakeCount());
            productProfit.setTotalFakeAmount(productProfit.getTotalFakeAmount().add(item.getTotalFakeAmount()));
            productProfit.setTotalPersonalFakeCount(productProfit.getTotalPersonalFakeCount() + item.getTotalPersonalFakeCount());
            productProfit.setTotalPersonalFakeAmount(productProfit.getTotalPersonalFakeAmount().add(item.getTotalPersonalFakeAmount()));
            productProfit.setTotalPersonalFakeEnablingCount(productProfit.getTotalPersonalFakeEnablingCount() + item.getTotalPersonalFakeEnablingCount());
            productProfit.setTotalPersonalFakeEnablingAmount(productProfit.getTotalPersonalFakeEnablingAmount().add(item.getTotalPersonalFakeEnablingAmount()));

            productProfit.setTotalBrokerage(productProfit.getTotalBrokerage().add(item.getTotalBrokerage()));
            productProfit.setTotalPrice(productProfit.getTotalPrice().add(item.getTotalPrice()));
            productProfit.setTotalCost(productProfit.getTotalCost().add(item.getTotalCost()));
            productProfit.setWrongCount(productProfit.getWrongCount() + item.getWrongCount());

        }));
        log.info(String.valueOf(new Date().getTime() - start.getTime()));


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

