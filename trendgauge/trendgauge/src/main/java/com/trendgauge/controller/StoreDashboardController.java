package com.trendgauge.controller;

import com.trendgauge.model.response.StoreRankingResponse;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StoreDashboardController {
    private final StoreDashboardService storeDashboardService;

    public StoreDashboardController(StoreDashboardService storeDashboardService){
        this.storeDashboardService = storeDashboardService;
    }

    @GetMapping("/main")
    public String storeDashboardPage(Model model){
        List<StoreRankingResponse> ranking = storeDashboardService.getRanking();
        List<StoreRankingResponse> budgetRanking = storeDashboardService.getBudgetRanking();
        model.addAttribute("ranking", ranking);
        model.addAttribute("budgetRanking", budgetRanking);
        return "dashboard/store";
    }


}
