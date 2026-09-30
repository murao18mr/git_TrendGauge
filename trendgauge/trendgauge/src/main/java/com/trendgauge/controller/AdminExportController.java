package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.response.AdminExportResponse;
import com.trendgauge.model.response.AdminMonthlyExportResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;
import com.trendgauge.service.AdminExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminExportController {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final AdminExportService adminExportService;

    public AdminExportController(StoreRepository storeRepository, UserRepository userRepository, AdminExportService adminExportService) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.adminExportService = adminExportService;
    }

    @GetMapping("/export")
    public String exportPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();

        Long companyId = user.getCompanyId();
        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);

        String targetMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

        model.addAttribute("stores", stores);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", today);
        model.addAttribute("targetMonth", targetMonth);

        return "admin/export";
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(name = "data-type") String dataType,
            @RequestParam(name = "store-id", required = false) Long storeId,
            @RequestParam(name = "start-date", required = false) LocalDate startDate,
            @RequestParam(name = "end-date", required = false) LocalDate endDate,
            @RequestParam(name = "target-month", required = false) String targetMonth,
            @RequestParam String format,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = user.getCompanyId();

        HttpHeaders headers = new HttpHeaders();

        if ("daily".equals(dataType)) {
            List<AdminExportResponse> data = adminExportService.createDailyData(companyId, storeId, startDate, endDate);

            if ("pdf".equals(format)) {

                String storeName = "全店舗";

                if (storeId != null) {
                    storeName = storeRepository.findById(storeId)
                            .orElseThrow()
                            .getStoreName();
                }
                byte[] pdf = adminExportService.createDailyPdf(data, startDate, endDate, storeName);
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily-sales.pdf");
                return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
            }

            byte[] csv = adminExportService.createDailyCsv(data);
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily-sales.csv");
            return new ResponseEntity<>(csv, headers, HttpStatus.OK);
        }

        if ("monthly".equals(dataType)) {
            List<AdminMonthlyExportResponse> data = adminExportService.createMonthlyData(companyId, storeId, targetMonth);

            if ("pdf".equals(format)) {
                String storeName = "全店舗";
                if (storeId != null) {
                    storeName = storeRepository.findById(storeId)
                            .orElseThrow()
                            .getStoreName();
                }

                byte[] pdf = adminExportService.createMonthlyPdf(data, targetMonth, storeName);
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-sales.pdf");

                return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
            }

            byte[] csv = adminExportService.createMonthlyCsv(data);
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly-sales.csv");

            return new ResponseEntity<>(csv, headers, HttpStatus.OK);
        }

        return ResponseEntity.badRequest().build();
    }
}
