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
import org.springframework.web.bind.annotation.ResponseBody;

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

        Long dailyTarget = storeDashboardService.dailyTarget(storeId);
        List<MemoEntity> todayMemos = memoService.getTodayMemos(storeId, LocalDate.now());

        model.addAttribute("baseDate", baseDate);
        model.addAttribute("currentWeekStart", currentWeekStart);
        model.addAttribute("currentWeekEnd", currentWeekEnd);
        model.addAttribute("previousWeekDate", previousWeekDate);
        model.addAttribute("nextWeekDate", nextWeekDate);
        model.addAttribute("canGoNextWeek", canGoNextWeek);
        model.addAttribute("view", view);

        model.addAttribute("dailyTarget", dailyTarget);
        model.addAttribute("todayMemos", todayMemos);

        return "dashboard/store";
    }

    @GetMapping("/main/sales")
    @ResponseBody
    public List<SalesTrendResponse> getSalesTrend(
            Authentication authentication,
            @RequestParam(required = false) LocalDate date
    ){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        LocalDate baseDate = date != null ? date : LocalDate.now();

        return storeDashboardService.getWeeklySales(storeId, baseDate);
    }

    @GetMapping("/main/category")
    @ResponseBody
    public List<CategorySalesResponse> getCategorySales(
            Authentication authentication,
            @RequestParam(required = false) LocalDate date
    ){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        LocalDate baseDate = date != null ? date : LocalDate.now();

        return storeDashboardService.getCategorySales(storeId, baseDate);
    }

    @GetMapping("/main/color")
    @ResponseBody
    public List<ColorSalesResponse> getColorSales(
            Authentication authentication,
            @RequestParam(required = false) LocalDate date
    ){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        LocalDate baseDate = date != null ? date : LocalDate.now();

        return storeDashboardService.getColorSales(storeId, baseDate);
    }

    @GetMapping("/main/ratio")
    @ResponseBody
    public List<StoreRankingResponse> getRatioRanking(Authentication authentication){
        String storeCode = authentication.getName();
        Long companyId = storeDashboardService.getCompanyId(storeCode);
        return storeDashboardService.getRatioRanking(companyId);
    }

    @GetMapping("/main/budget")
    @ResponseBody
    public List<StoreRankingResponse> getBudgetRanking(Authentication authentication){
        String storeCode = authentication.getName();
        Long companyId = storeDashboardService.getCompanyId(storeCode);

        return storeDashboardService.getBudgetRanking(companyId);
    }

}
