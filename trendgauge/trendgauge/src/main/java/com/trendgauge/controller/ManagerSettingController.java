package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.request.DailyTargetInput;
import com.trendgauge.model.request.ManagerPinInput;
import com.trendgauge.model.request.TargetInput;
import com.trendgauge.model.response.TargetResponse;
import com.trendgauge.service.ManagerPinService;
import com.trendgauge.service.ManagerSettingService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/setting")
public class ManagerSettingController {
    private final StoreDashboardService storeDashboardService;
    private final ManagerSettingService managerSettingService;
    private final ManagerPinService managerPinService;

    public ManagerSettingController(StoreDashboardService storeDashboardService, ManagerSettingService managerSettingService, ManagerPinService managerPinService) {
        this.storeDashboardService = storeDashboardService;
        this.managerSettingService = managerSettingService;
        this.managerPinService = managerPinService;
    }

    @GetMapping
    public String settingPage(Model model, Authentication authentication) {
        String storeCode = authentication.getName();
        StoreEntity store = storeDashboardService.getStore(storeCode);
        model.addAttribute("store", store);
        model.addAttribute("targetInput", new TargetInput());
        model.addAttribute("managerPinInput", new ManagerPinInput());
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
            @Validated TargetInput input,
            BindingResult br,
            Authentication authentication,
            RedirectAttributes ra,
            Model model
    ) {
        if (br.hasErrors()) {
            String storeCode = authentication.getName();
            StoreEntity store = storeDashboardService.getStore(storeCode);
            model.addAttribute("store", store);
            model.addAttribute("targetInput", input);
            return "manager/setting";
        }
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        managerSettingService.saveTarget(storeId, input);

        ra.addFlashAttribute("successMessage", "月間予算を保存しました。");
        return "redirect:/manager/setting";
    }

    @GetMapping("/daily-target")
    @ResponseBody
    public List<DailyTargetInput> getDailyTargets(
            @RequestParam String targetMonth,
            Authentication authentication
    ) {
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        return managerSettingService.getDailyTargets(storeId, targetMonth);
    }

    @PostMapping("/daily-target")
    @ResponseBody
    public void saveDailyTargets(
            @RequestBody List<DailyTargetInput> inputs,
            Authentication authentication
    ) {
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);
        managerSettingService.saveDailyTargets(storeId, inputs);
    }

    @PostMapping("/pin")
    public String changePin(
            Authentication authentication,
            @Validated @ModelAttribute("managerPinInput") ManagerPinInput input,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        String storeCode = authentication.getName();

        if (br.hasErrors()) {
            StoreEntity store = storeDashboardService.getStore(storeCode);
            model.addAttribute("store", store);
            model.addAttribute("targetInput", new TargetInput());
            model.addAttribute("scrollToError", true);
            return "manager/setting";
        }

        if (!managerPinService.changePin(storeCode, input.getPin(), input.getNewPin())) {
            br.rejectValue("pin", "invalid", "現在のPINコードが正しくありません");
            StoreEntity store = storeDashboardService.getStore(storeCode);
            model.addAttribute("store", store);
            model.addAttribute("targetInput", new TargetInput());
            model.addAttribute("scrollToError", true);
            return "manager/setting";
        }

        ra.addFlashAttribute("successMessage", "PINコードを変更しました");
        return "redirect:/manager/setting";
    }
}
