package com.trendgauge.controller;

import com.trendgauge.model.response.WeeklyReportResponse;
import com.trendgauge.service.ManagerReportService;
import com.trendgauge.service.SlackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@Controller
@RequestMapping("/manager/reports")
public class ManagerReportController {
    private final ManagerReportService managerReportService;
    private final SlackService slackService;

    public ManagerReportController(ManagerReportService managerReportService, SlackService slackService){
        this.managerReportService = managerReportService;
        this.slackService = slackService;
    }

    @GetMapping
    public String weeklyReportPage(Model model){
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate startDate = endDate.minusDays(6);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "manager/reports";
    }

    @GetMapping("/generate")
    @ResponseBody
    public WeeklyReportResponse generateReport(Authentication authentication) {
        String storeCode = authentication.getName();
        Long storeId = managerReportService.getStoreId(storeCode);

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        LocalDate startDate = endDate.minusDays(6);

        String storeName = managerReportService.getStoreName(storeCode);
        Long weeklySales = managerReportService.getWeeklySales(storeId, startDate, endDate);
        BigDecimal weeklyBudgetRatio = managerReportService.getWeeklyBudgetRatio(storeId, startDate,endDate);
        BigDecimal weeklyRatio = managerReportService.getWeeklyRatio(storeId, startDate, endDate);
        String topCategory = managerReportService.getTopCategory(storeId, startDate, endDate);

        return new WeeklyReportResponse(storeName, startDate, endDate, weeklySales, weeklyBudgetRatio, weeklyRatio, topCategory);
    }

    @PostMapping("/slack")
    @ResponseBody
    public ResponseEntity<?> sendToSlack(@RequestBody Map<String, String> request) {
        String reportText = request.get("reportText");

        if (reportText == null || reportText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        slackService.sendMessage(reportText);
        return ResponseEntity.ok().build();
    }
}
