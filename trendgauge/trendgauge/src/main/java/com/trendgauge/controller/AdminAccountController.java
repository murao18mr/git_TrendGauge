package com.trendgauge.controller;

import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.request.AdminAccountInput;
import com.trendgauge.model.request.AdminPasswordResetInput;
import com.trendgauge.model.request.AdminStoreEditInput;
import com.trendgauge.model.response.StoreAccountResponse;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminAccountController {
    private final AdminAccountService adminAccountService;
    private final UserRepository userRepository;

    public AdminAccountController(AdminAccountService adminAccountService, UserRepository userRepository){
        this.adminAccountService = adminAccountService;
        this.userRepository = userRepository;
    }

    @GetMapping("/account")
    public String accountPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        List<StoreAccountResponse> storeAccounts = adminAccountService.getStoreAccounts(companyId);

        model.addAttribute("storeAccounts", storeAccounts);
        model.addAttribute("adminAccountInput", new AdminAccountInput());
        model.addAttribute("adminPasswordResetInput", new AdminPasswordResetInput());

        return "admin/account";
    }

    @PostMapping("/account")
    public String registerAccount(
            Authentication authentication,
            @Validated AdminAccountInput input,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();
        
        if (adminAccountService.isStoreCodeUsed(input.getStoreCode())) {
            br.rejectValue("storeCode", "duplicate", "この店舗コードはすでに使用されています");
        }

        if (br.hasErrors()) {
            List<StoreAccountResponse> storeAccounts = adminAccountService.getStoreAccounts(companyId);
            model.addAttribute("storeAccounts", storeAccounts);
            return "admin/account";
        }

        adminAccountService.registerAccount(input, companyId);
        ra.addFlashAttribute("successMessage", "店舗アカウントを登録しました");

        return "redirect:/admin/account";
    }

    @PostMapping("/account/edit")
    @ResponseBody
    public ResponseEntity<String> updateStore(
            @RequestBody AdminStoreEditInput input,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        try {
            adminAccountService.updateStore(companyId, input);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/account/password-reset")
    @ResponseBody
    public ResponseEntity<String> resetPassword(
            @RequestBody AdminPasswordResetInput input,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        try {
            adminAccountService.resetPassword(input, companyId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
