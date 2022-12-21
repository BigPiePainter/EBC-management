package com.pofa.ebcadmin.profitReport.service;

import com.pofa.ebcadmin.profitReport.entity.MismatchedSkusInfo;
import com.pofa.ebcadmin.profitReport.entity.ProfitReportInfo;

import java.util.Date;
import java.util.List;

public interface ProfitReportService {

    List<ProfitReportInfo> getProfitReport(Date startDate, Date endDate);


    List<MismatchedSkusInfo> getMismatchedSkus(Date date, Long productId);


}
