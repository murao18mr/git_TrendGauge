package com.trendgauge.controller;

import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;
    private final UserRepository userRepository;

    public AdminDashboardController(AdminDashboardService adminDashboardService, UserRepository userRepository) {
        this.adminDashboardService = adminDashboardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/main")
    public String adminDashboardPage(Authentication authentication, Model model) {
        String email = authentication.getName();

        Optional<UserEntity> user = userRepository.findByEmailAndRole(email, "admin");
        if (user.isEmpty()) {
            return "redirect:/admin/login";
        }

        Long companyId = user.get().getCompanyId();

        model.addAttribute("monthlyTotalSales", adminDashboardService.getMonthlyTotalSales(companyId));
        model.addAttribute("monthlyBudgetRatio", adminDashboardService.calculateBudgetRatio(companyId));
        model.addAttribute("activeStoreCount", adminDashboardService.getActiveStoreCount(companyId));
        model.addAttribute("budgetRanking", adminDashboardService.getTopRanking(companyId));
        return "dashboard/admin";
    }

}
