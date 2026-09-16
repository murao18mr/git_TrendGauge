package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.repository.StoreRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/sales")
public class CsvImportController {
    private final StoreRepository storeRepository;

    public CsvImportController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/import")
    public String importPage(Authentication authentication, Model model){
        String storeCode = authentication.getName();
        Optional<StoreEntity> store = storeRepository.findByStoreCode(storeCode);

        String storeName = store.get().getStoreName();
        model.addAttribute("storeName", storeName);
        return "sales/import";
    }
}
