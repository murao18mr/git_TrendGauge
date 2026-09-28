package com.trendgauge.controller;

import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.response.AdminRankingResponse;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminRankingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminRankingController {
    private final AdminRankingService adminRankingService;
    private final UserRepository userRepository;

    public AdminRankingController(AdminRankingService adminRankingService, UserRepository userRepository) {
        this.adminRankingService = adminRankingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/ranking")
    public String rankingPage(
            @RequestParam(required = false) String targetMonth,
            @RequestParam(required = false, defaultValue = "sales") String sortOrder,
            Authentication authentication,
            Model model
    ) {
        if (targetMonth == null) {
            targetMonth = YearMonth.now().toString();
        }
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        List<AdminRankingResponse> ranking = adminRankingService.getRanking(companyId, targetMonth, sortOrder);

        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("ranking", ranking);

        return "admin/ranking";
    }

}
