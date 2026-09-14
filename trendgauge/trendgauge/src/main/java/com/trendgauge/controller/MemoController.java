package com.trendgauge.controller;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.service.MemoService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/main")
public class MemoController {
    private final MemoService memoService;
    private final StoreDashboardService storeDashboardService;

    public MemoController(MemoService memoService, StoreDashboardService storeDashboardService){
        this.memoService = memoService;
        this.storeDashboardService = storeDashboardService;
    }

    @PostMapping("/memo")
    public ResponseEntity<Void> registerMemo(
            Authentication authentication,
            @RequestParam LocalDate saleDate,
            @RequestParam String comment
    ){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        memoService.registerMemo(storeId, saleDate, comment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/memo")
    @ResponseBody
    public List<MemoEntity> getTodayMemos(Authentication authentication){
        String storeCode = authentication.getName();
        Long storeId = storeDashboardService.getStoreId(storeCode);

        return memoService.getTodayMemos(storeId, LocalDate.now());
    }


}
