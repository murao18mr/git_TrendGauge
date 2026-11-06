package com.trendgauge.controller;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.model.request.SalesEditInput;
import com.trendgauge.model.response.KeywordResponse;
import com.trendgauge.model.response.KeywordResultResponse;
import com.trendgauge.model.response.ManagerSalesResponse;
import com.trendgauge.service.GeminiAnalysisService;
import com.trendgauge.service.ManagerSalesService;
import com.trendgauge.service.MemoService;
import com.trendgauge.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager/sales")
public class ManagerSalesController {
    private final ManagerSalesService managerSalesService;
    private final GeminiAnalysisService geminiAnalysisService;

    public ManagerSalesController(
            ManagerSalesService managerSalesService,
            GeminiAnalysisService geminiAnalysisService) {

        this.managerSalesService = managerSalesService;
        this.geminiAnalysisService = geminiAnalysisService;
    }

    @GetMapping("/edit")
    public String editPage(Model model, Authentication authentication) {
        String storeCode = authentication.getName();
        List<ManagerSalesResponse> sales = managerSalesService.getSales(storeCode);

        model.addAttribute("sales", sales);

        return "manager/sales-edit";
    }

    @GetMapping("/{saleId}/memos")
    @ResponseBody
    public List<MemoEntity> getMemos(@PathVariable Long saleId,
                                     Authentication authentication) {
        String storeCode = authentication.getName();
        return managerSalesService.getMemos(storeCode, saleId);
    }

    @GetMapping("/{saleId}/report")
    @ResponseBody
    public ResponseEntity<ReportEntity> getReport(@PathVariable Long saleId,
                                                  Authentication authentication) {
        String storeCode = authentication.getName();
        ReportEntity report = managerSalesService.getReport(storeCode, saleId);

        if (report == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(report);
    }

    @PostMapping("/edit")
    @ResponseBody
    public void updateSales(@RequestBody SalesEditInput input, Authentication authentication) {
        String storeCode = authentication.getName();
        managerSalesService.updateSale(storeCode, input);
    }

    @GetMapping("/keywords")
    @ResponseBody
    public List<KeywordResponse> getKeywords(Authentication authentication) {
        String storeCode = authentication.getName();
        return managerSalesService.getKeywords(storeCode);
    }

    @GetMapping("/keywords/{keyword}")
    @ResponseBody
    public List<KeywordResultResponse> searchKeyword(
            @PathVariable String keyword,
            Authentication authentication) {
        String storeCode = authentication.getName();
        return managerSalesService.searchKeyword(storeCode, keyword);
    }

    @PostMapping("/ai-analysis")
    @ResponseBody
    public ResponseEntity<String> analyzeMemos(Authentication authentication) {

        String storeCode = authentication.getName();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        String text = managerSalesService.getAnalysisText(
                storeCode, startDate, endDate);

        if (text.isBlank()) {
            return ResponseEntity.ok("分析対象の日報・メモがありません。");
        }

        String result = geminiAnalysisService.analyzeStoreMemos(text);

        return ResponseEntity.ok(result);
    }
}
