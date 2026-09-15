package com.trendgauge.controller;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.request.MemoInput;
import com.trendgauge.service.MemoService;
import com.trendgauge.service.StoreDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
public class MemoController {
    private final MemoService memoService;
    private final StoreDashboardService storeDashboardService;

    public MemoController(MemoService memoService, StoreDashboardService storeDashboardService) {
        this.memoService = memoService;
        this.storeDashboardService = storeDashboardService;
    }

    @PostMapping("/memo")
    @ResponseBody
    public ResponseEntity<?> registerMemo(
            Authentication authentication,
            @Validated MemoInput input,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            if (br.hasFieldErrors("saleDate")) {
                errors.put(
                        "saleDate",
                        br.getFieldError("saleDate").getDefaultMessage()
                );
            }
            if (br.hasFieldErrors("comment")) {
                errors.put(
                        "comment",
                        br.getFieldError("comment").getDefaultMessage()
                );
            }
            return ResponseEntity.badRequest().body(errors);
        }
            String storeCode = authentication.getName();
            Long storeId = storeDashboardService.getStoreId(storeCode);

            memoService.registerMemo(
                    storeId,
                    input.getSaleDate(),
                    input.getComment()
            );

            return ResponseEntity.ok().build();
        }

        @GetMapping("/memo")
        @ResponseBody
        public List<MemoEntity> getTodayMemos (Authentication authentication){
            String storeCode = authentication.getName();
            Long storeId = storeDashboardService.getStoreId(storeCode);

            return memoService.getTodayMemos(storeId, LocalDate.now());
        }


    }
