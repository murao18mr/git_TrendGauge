package com.trendgauge.controller;

import com.trendgauge.model.entity.MappingEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.response.CsvPreviewResponse;
import com.trendgauge.model.response.CsvPreviewResult;
import com.trendgauge.repository.StoreRepository;

import com.trendgauge.service.CsvImportService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sales")
public class CsvImportController {
    private final StoreRepository storeRepository;
    private final CsvImportService csvImportService;

    public CsvImportController(StoreRepository storeRepository, CsvImportService csvImportService) {
        this.storeRepository = storeRepository;
        this.csvImportService = csvImportService;
    }

    @GetMapping("/import")
    public String importPage(Authentication authentication, Model model) {
        String storeCode = authentication.getName();
        Optional<StoreEntity> store = storeRepository.findByStoreCode(storeCode);

        String storeName = store.get().getStoreName();
        model.addAttribute("storeName", storeName);
        return "sales/import";
    }

    @PostMapping("/preview")
    @ResponseBody
    public CsvPreviewResult previewCsv(
            Authentication authentication,
            @RequestParam("csvFile") MultipartFile csvFile,
            @RequestParam(value = "mappingColumns", required = false) List<String> mappingColumns
    ) throws IOException {
        String storeCode = authentication.getName();
        if (mappingColumns != null && !mappingColumns.isEmpty()) {
            MappingEntity mapping = csvImportService.createMappingEntity(mappingColumns);
            return csvImportService.checkCsvWithMapping(csvFile, mapping);
        }
        return csvImportService.checkCsv(csvFile, storeCode);
    }

    @PostMapping("/import")
    public String importCsv(
            Authentication authentication,
            @RequestParam("csvFile") MultipartFile csvFile,
            @RequestParam(value = "mappingColumns", required = false) List<String> mappingColumns,
            RedirectAttributes ra,
            Model model
    ) {
        String storeCode = authentication.getName();
        if (csvFile.isEmpty()) {
            return "sales/import";
        }

        String fileName = csvFile.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return "sales/import";
        }

        if (csvFile.getSize() > 100 * 1024 * 1024) {
            return "sales/import";
        }

        try {
            CsvPreviewResult previewResult;
            MappingEntity mapping = null;

            if (mappingColumns != null && !mappingColumns.isEmpty()) {
                mapping = csvImportService.createMappingEntity(mappingColumns);
                previewResult = csvImportService.checkCsvWithMapping(csvFile, mapping);
            } else {
                previewResult = csvImportService.checkCsv(csvFile, storeCode);
            }

            List<CsvPreviewResponse> previewRows = previewResult.getRows();
            boolean hasError = previewRows.stream().anyMatch(row -> row.getErrorMessage() != null);
            if (hasError) {
                model.addAttribute("previewRows", previewRows);
                return "sales/import";
            }
            if (mapping != null) {
                csvImportService.createMapping(mappingColumns, storeCode);
            }
            csvImportService.importCsv(previewRows, storeCode);
            ra.addFlashAttribute("successMessage", "CSVの取り込みが完了しました");
        } catch (IOException e) {
            return "sales/import";
        }
        return "redirect:/main";
    }
}
