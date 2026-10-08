package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.request.AdminPosSettingInput;
import com.trendgauge.model.response.AdminPosSettingResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminPosSettingService;
import com.trendgauge.service.SmaregiApiService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/setting")
public class AdminPosSettingController {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final AdminPosSettingService adminPosSettingService;
    private final SmaregiApiService smaregiApiService;

    public AdminPosSettingController(UserRepository userRepository, StoreRepository storeRepository, AdminPosSettingService adminPosSettingService, SmaregiApiService smaregiApiService) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.adminPosSettingService = adminPosSettingService;
        this.smaregiApiService = smaregiApiService;
    }

    @GetMapping("/pos")
    public String posSettingPage(
            @RequestParam(required = false) Long storeId,
            Authentication authentication,
            Model model
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);
        if (storeId != null) {
            boolean exists = stores.stream()
                    .anyMatch(store -> store.getId().equals(storeId));

            if (!exists) {
                throw new IllegalArgumentException("対象店舗が見つかりません");
            }
        }

        AdminPosSettingInput input = new AdminPosSettingInput();
        input.setStoreId(storeId);

        model.addAttribute("stores", stores);
        model.addAttribute("adminPosSettingInput", input);

        return "admin/pos-setting";
    }


    @GetMapping("/pos/{storeId}")
    @ResponseBody
    public AdminPosSettingResponse getPosSetting(
            @PathVariable Long storeId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        return adminPosSettingService.getSetting(storeId, companyId);
    }


    @PostMapping("/pos")
    public String savePosSetting(
            @Validated @ModelAttribute("adminPosSettingInput")AdminPosSettingInput input,
            BindingResult br,
            Authentication authentication,
            RedirectAttributes ra,
            Model model
    ) throws Exception {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        if (input.getStoreId() != null) {
            AdminPosSettingResponse setting =
                    adminPosSettingService.getSetting(input.getStoreId(), companyId);

            boolean hasSecret = input.getClientSecret() != null
                    && !input.getClientSecret().isBlank();

            if (!setting.isRegistered() && !hasSecret) {
                br.rejectValue("clientSecret", "required",
                        "クライアントシークレットは必須入力です");
            }
        }

        if (br.hasErrors()) {
            input.setClientSecret(null);
            model.addAttribute("stores", storeRepository.findByCompanyId(companyId));
            return "admin/pos-setting";
        }

        adminPosSettingService.saveSetting(input, companyId);
        ra.addFlashAttribute("successMessage", "POSレジ連携設定を保存しました");

        return "redirect:/admin/setting/pos";
    }


    @PostMapping("/pos/test")
    @ResponseBody
    public String testPosSetting(
            @RequestBody AdminPosSettingInput input,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        if (input.getStoreId() == null
                || !"smaregi".equals(input.getPosType())
                || input.getContractId() == null || input.getContractId().isBlank()
                || input.getClientId() == null || input.getClientId().isBlank()) {
            return "入力内容を確認してください";
        }

        try {
            String secret = adminPosSettingService.getTestSecret(input, companyId);

            boolean success = smaregiApiService.testConnection(
                    input.getContractId(),
                    input.getClientId(),
                    secret
            );

            return success ? "接続に成功しました" : "接続に失敗しました。認証情報を確認してください";

        } catch (IllegalArgumentException e) {
            return "入力内容を確認してください";

        } catch (Exception e) {
            return "接続テスト中にエラーが発生しました";
        }
    }

    @PostMapping("/pos/sync")
    @ResponseBody
    public String syncSales(
            @RequestParam Long storeId,
            Authentication authentication
    ) throws Exception {

        String email = authentication.getName();

        UserEntity admin = userRepository.findByEmailAndRole(email, "admin")
                .orElseThrow();

        Long companyId = admin.getCompanyId();

        String result = adminPosSettingService.syncSales(storeId, companyId);

        return result;
    }
}
