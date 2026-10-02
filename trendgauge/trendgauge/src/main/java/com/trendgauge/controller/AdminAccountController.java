package com.trendgauge.controller;

import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.request.AdminAccountInput;
import com.trendgauge.model.response.StoreAccountResponse;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
