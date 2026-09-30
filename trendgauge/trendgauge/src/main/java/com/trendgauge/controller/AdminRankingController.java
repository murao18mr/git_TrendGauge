package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.response.AdminRankingResponse;
import com.trendgauge.model.response.CategorySalesResponse;
import com.trendgauge.model.response.ColorSalesResponse;
import com.trendgauge.model.response.SalesTrendResponse;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminRankingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
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


    @GetMapping("/store-detail")
    public String storeDetailPage(
            @RequestParam Long storeId,
            Authentication authentication,
            Model model
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        StoreEntity store = adminRankingService.getStore(storeId, companyId);

        LocalDate baseDate = LocalDate.now();
        LocalDate currentWeekStart = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
        LocalDate nextWeekDate = currentWeekEnd.plusDays(1);
        boolean canGoNextWeek = !nextWeekDate.isAfter(LocalDate.now());

        model.addAttribute("store", store);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("currentWeekStart", currentWeekStart);
        model.addAttribute("currentWeekEnd", currentWeekEnd);
        model.addAttribute("canGoNextWeek", canGoNextWeek);

        return "admin/store-detail";
    }

    @GetMapping("/store-detail/sales")
    @ResponseBody
    public List<SalesTrendResponse> getStoreSales(
            @RequestParam Long storeId,
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        adminRankingService.getStore(storeId, companyId);

        LocalDate baseDate = date != null ? date : LocalDate.now();
        return adminRankingService.getStoreSales(storeId, baseDate);
    }

    @GetMapping("/store-detail/category")
    @ResponseBody
    public List<CategorySalesResponse> getStoreCategorySales(
            @RequestParam Long storeId,
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        adminRankingService.getStore(storeId, companyId);

        LocalDate baseDate = date != null ? date : LocalDate.now();
        return adminRankingService.getStoreCategorySales(storeId, baseDate);
    }

    @GetMapping("/store-detail/color")
    @ResponseBody
    public List<ColorSalesResponse> getStoreColorSales(
            @RequestParam Long storeId,
            @RequestParam(required = false) LocalDate date,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        adminRankingService.getStore(storeId, companyId);

        LocalDate baseDate = date != null ? date : LocalDate.now();
        return adminRankingService.getStoreColorSales(storeId, baseDate);
    }

    @GetMapping("/ranking/csv")
    @ResponseBody
    public ResponseEntity<byte[]> downloadCsv(
            @RequestParam String targetMonth,
            @RequestParam(defaultValue = "sales") String sortOrder,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        List<AdminRankingResponse> ranking = adminRankingService.getRanking(companyId, targetMonth, sortOrder);

        byte[] csv = adminRankingService.createCsv(ranking);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ranking.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
