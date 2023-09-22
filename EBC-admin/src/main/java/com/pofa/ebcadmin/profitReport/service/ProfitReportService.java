package com.pofa.ebcadmin.profitReport.service;

import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;
import com.pofa.ebcadmin.user.entity.UserInfo;

import java.util.Date;
import java.util.List;

public interface ProfitReportService {

    List<ProfitReportInfo> getProfitReportByUser(Date startDate, Date endDate, UserInfo user);

    List<ProfitReportInfo> getProfitReport(Date startDate, Date endDate);


    List<MismatchedSkusInfo> getMismatchedSkus(Date date, Long productId);


}
