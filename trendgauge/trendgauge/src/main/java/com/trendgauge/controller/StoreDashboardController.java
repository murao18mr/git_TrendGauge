package com.trendgauge.controller;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.response.CategorySalesResponse;
import com.trendgauge.model.response.ColorSalesResponse;
import com.trendgauge.model.response.SalesTrendResponse;
import com.trendgauge.model.response.StoreRankingResponse;
import com.trendgauge.service.MemoService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
public class StoreDashboardController {
    private final StoreDashboardService storeDashboardService;
    private final MemoService memoService;

    public StoreDashboardController(StoreDashboardService storeDashboardService, MemoService memoService){
        this.storeDashboardService = storeDashboardService;
        this.memoService = memoService;
    }

    @GetMapping("/main")
    public String storeDashboardPage(
            Model model,
            Authentication authentication,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String view
    ){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        LocalDate baseDate = date != null ? date : LocalDate.now();
        LocalDate currentWeekStart = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
        LocalDate previousWeekDate = currentWeekStart.minusDays(1);
        LocalDate nextWeekDate = currentWeekEnd.plusDays(1);
        boolean canGoNextWeek = !nextWeekDate.isAfter(LocalDate.now());

        List<StoreRankingResponse> ratioRanking = storeDashboardService.getRatioRanking();
        List<StoreRankingResponse> budgetRanking = storeDashboardService.getBudgetRanking();
        List<SalesTrendResponse> weeklySales = storeDashboardService.getWeeklySales(storeId, baseDate);
        List<CategorySalesResponse> categorySales = storeDashboardService.getCategorySales(storeId, baseDate);
        List<ColorSalesResponse> colorSales = storeDashboardService.getColorSales(storeId, baseDate);

        Long dailyTarget = storeDashboardService.dailyTarget(storeId);
        List<MemoEntity> todayMemos = memoService.getTodayMemos(storeId, LocalDate.now());


        model.addAttribute("ratioRanking", ratioRanking);
        model.addAttribute("budgetRanking", budgetRanking);

        model.addAttribute("weeklySales", weeklySales);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("currentWeekStart", currentWeekStart);
        model.addAttribute("currentWeekEnd", currentWeekEnd);
        model.addAttribute("previousWeekDate", previousWeekDate);
        model.addAttribute("nextWeekDate", nextWeekDate);
        model.addAttribute("canGoNextWeek", canGoNextWeek);
        model.addAttribute("view", view);

        model.addAttribute("categorySales", categorySales);
        model.addAttribute("colorSales", colorSales);

        model.addAttribute("dailyTarget", dailyTarget);
        model.addAttribute("todayMemos", todayMemos);

        return "dashboard/store";
    }


}
