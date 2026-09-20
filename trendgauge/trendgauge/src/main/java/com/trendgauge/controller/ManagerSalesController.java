package com.trendgauge.controller;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.model.request.SalesEditInput;
import com.trendgauge.model.response.ManagerSalesResponse;
import com.trendgauge.service.ManagerSalesService;
import com.trendgauge.service.MemoService;
import com.trendgauge.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager/sales")
public class ManagerSalesController {
    private final ManagerSalesService managerSalesService;
    private final MemoService memoService;
    private final ReportService reportService;

    public ManagerSalesController(ManagerSalesService managerSalesService, MemoService memoService, ReportService reportService){
        this.managerSalesService = managerSalesService;
        this.memoService = memoService;
        this.reportService = reportService;
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
    public List<MemoEntity> getMemos(@PathVariable Long saleId){
        return memoService.getMemos(saleId);
    }

    @GetMapping("/{saleId}/report")
    @ResponseBody
    public ResponseEntity<ReportEntity> getReport(@PathVariable Long saleId) {
        ReportEntity report = reportService.getReport(saleId);

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
}
