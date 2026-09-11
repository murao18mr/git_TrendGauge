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

import java.time.LocalDate;
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
    public String storeDashboardPage(Model model, Authentication authentication){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        List<StoreRankingResponse> ratioRanking = storeDashboardService.getRatioRanking();
        List<StoreRankingResponse> budgetRanking = storeDashboardService.getBudgetRanking();
        List<SalesTrendResponse> weeklySales = storeDashboardService.getWeeklySales(storeId);
        List<CategorySalesResponse> categorySales = storeDashboardService.getCategorySales(storeId);
        List<ColorSalesResponse> colorSales = storeDashboardService.getColorSales(storeId);

        Long dailyTarget = storeDashboardService.dailyTarget(storeId);
        List<MemoEntity> todayMemos = memoService.getTodayMemos(storeId, LocalDate.now());


        model.addAttribute("ratioRanking", ratioRanking);
        model.addAttribute("budgetRanking", budgetRanking);
        model.addAttribute("weeklySales", weeklySales);
        model.addAttribute("categorySales", categorySales);
        model.addAttribute("colorSales", colorSales);

        model.addAttribute("dailyTarget", dailyTarget);
        model.addAttribute("todayMemos", todayMemos);

        return "dashboard/store";
    }


}
