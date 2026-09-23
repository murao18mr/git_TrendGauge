package com.trendgauge.controller;

import com.trendgauge.model.request.DailyReportInput;
import com.trendgauge.service.DailyReportService;
import com.trendgauge.service.MemoService;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/sales")
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final StoreDashboardService storeDashboardService;
    private final MemoService memoService;

    public DailyReportController(DailyReportService dailyReportService, StoreDashboardService storeDashboardService, MemoService memoService){
        this.dailyReportService = dailyReportService;
        this.storeDashboardService = storeDashboardService;
        this.memoService = memoService;
    }

    @GetMapping("/new")
    public String dailyReport(Model model, Authentication authentication){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        Long dailyTarget = storeDashboardService.dailyTarget(storeId);

        model.addAttribute("dailyReportInput", new DailyReportInput());
        model.addAttribute("dailyTarget", dailyTarget);
        model.addAttribute("todayMemos", memoService.getTodayMemos(storeId, LocalDate.now()));
        return "sales/daily-report";
    }

    @PostMapping("/new")
    public String registerDailyReport(
            Authentication authentication,
            @Validated @ModelAttribute("dailyReportInput") DailyReportInput dailyReportInput,
            BindingResult br,
            RedirectAttributes ra,
            Model model
    ){
        if(br.hasErrors()){
            String storeCode = authentication.getName();
            Long storeId = storeDashboardService.getStoreId(storeCode);
            Long dailyTarget = storeDashboardService.dailyTarget(storeId);

            model.addAttribute("dailyTarget", dailyTarget);
            model.addAttribute("todayMemos", memoService.getTodayMemos(storeId, LocalDate.now()));
            return "sales/daily-report";
        }
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        dailyReportService.registerDailyReport(storeId,dailyReportInput);
        ra.addFlashAttribute("successMessage", "日次報告の登録が完了しました");
        return "redirect:/main";
    }
}