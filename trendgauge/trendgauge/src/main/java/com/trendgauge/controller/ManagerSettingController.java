package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.request.TargetInput;
import com.trendgauge.model.response.TargetResponse;
import com.trendgauge.service.ManagerSettingService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/manager/setting")
public class ManagerSettingController {
    private final StoreDashboardService storeDashboardService;
    private final ManagerSettingService managerSettingService;

    public ManagerSettingController (StoreDashboardService storeDashboardService, ManagerSettingService managerSettingService){
        this.storeDashboardService = storeDashboardService;
        this.managerSettingService = managerSettingService;
    }

    @GetMapping
    public String settingPage(Model model, Authentication authentication){
        String storeCode = authentication.getName();
        StoreEntity store = storeDashboardService.getStore(storeCode);
        model.addAttribute("store", store);
        return "manager/setting";
    }

    @GetMapping("/target")
    @ResponseBody
    public ResponseEntity<TargetResponse> getTarget(
            @RequestParam String targetMonth,
            Authentication authentication
    ) {
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        Optional<TargetEntity> target = managerSettingService.getTarget(storeId, targetMonth);

        if (target.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        TargetEntity entity = target.get();
        return ResponseEntity.ok(new TargetResponse(entity.getTargetAmount(), entity.getTargetRatio()));
    }

    @GetMapping("/target/ratio")
    @ResponseBody
    public ResponseEntity<BigDecimal> getTargetRatio(
            @RequestParam String targetMonth,
            @RequestParam Long targetAmount,
            Authentication authentication
    ) {
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        BigDecimal ratio = managerSettingService.calculateTargetRatio(storeId, targetMonth, targetAmount);

        if (ratio == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ratio);
    }

    @PostMapping("/target")
    public String saveTarget(
            TargetInput input,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        managerSettingService.saveTarget(storeId, input);

        redirectAttributes.addFlashAttribute("successMessage", "目標を保存しました。");

        return "redirect:/manager/setting";
    }
}
