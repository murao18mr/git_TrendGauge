package com.trendgauge.controller;

import com.trendgauge.model.request.DailyReportInput;
import com.trendgauge.service.DailyReportService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final StoreDashboardService storeDashboardService;

    public DailyReportController(DailyReportService dailyReportService, StoreDashboardService storeDashboardService){
        this.dailyReportService = dailyReportService;
        this.storeDashboardService = storeDashboardService;
    }

    @GetMapping("/new")
    public String dailyReport(Model model){
        model.addAttribute("dailyReportInput", new DailyReportInput());
        return "sales/daily-report";
    }

    @PostMapping("/new")
    public String registerDailyReport(
            Authentication authentication,
            @Validated @ModelAttribute("dailyReportInput") DailyReportInput dailyReportInput,
            BindingResult br,
            RedirectAttributes ra
    ){
        if(br.hasErrors()){
            return "sales/daily-report";
        }
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        dailyReportService.registerDailyReport(storeId,dailyReportInput);
        ra.addFlashAttribute("successMessage", "日次報告の登録が完了しました");
        return "redirect:/main";
    }
}