package com.trendgauge.service;

import com.trendgauge.model.entity.ItemEntity;
import com.trendgauge.model.entity.MappingEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.response.CsvPreviewResponse;
import com.trendgauge.model.response.CsvPreviewResult;
import com.trendgauge.repository.ItemRepository;
import com.trendgauge.repository.MappingRepository;
import com.trendgauge.repository.SaleRepository;
import com.trendgauge.repository.StoreRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {
    private final MappingRepository mappingRepository;
    private final StoreRepository storeRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService;
    private final ItemRepository itemRepository;

    public CsvImportService(MappingRepository mappingRepository, StoreRepository storeRepository, SaleRepository saleRepository, SaleService saleService, ItemRepository itemRepository) {
        this.mappingRepository = mappingRepository;
        this.storeRepository = storeRepository;
        this.saleRepository = saleRepository;
        this.saleService = saleService;
        this.itemRepository = itemRepository;
    }

    private String addError(String errorMessage, String newError) {
        if (errorMessage == null) {
            return newError;
        }
        return errorMessage + " / " + newError;
    }

    private boolean hasHeader(CSVRecord record, MappingEntity mapping) {
        try {
            LocalDate.parse(record.get(mapping.getDateColumn()));
            Long.parseLong(record.get(mapping.getAmountColumn()));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean mappingMatched(CSVRecord record, MappingEntity mapping) {
        try {
            LocalDate.parse(record.get(mapping.getDateColumn()));
            Long.parseLong(record.get(mapping.getAmountColumn()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMappingMatched(MultipartFile csvFile, MappingEntity mapping) throws IOException {
        try (InputStream inputStream = csvFile.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {

            int checkedRows = 0;

            for (CSVRecord record : parser) {
                if (mappingMatched(record, mapping)) {
                    return true;
                }

                checkedRows++;

                if (checkedRows >= 2) {
                    break;
                }
            }
        }

        return false;
    }

    private int getColumnCount(MultipartFile csvFile) throws IOException {
        try (InputStream inputStream = csvFile.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {

            for (CSVRecord record : parser) {
                return record.size();
            }
        }

        return 0;
    }

    public CsvPreviewResult checkCsv(MultipartFile csvFile, String storeCode) throws IOException {
        StoreEntity store = storeRepository.findByStoreCode(storeCode).orElseThrow();

        Long mappingId = store.getMappingId();
        if (mappingId == null) {
            throw new IllegalStateException("店舗にCSVマッピングが登録されていません");
        }
        MappingEntity mapping = mappingRepository.findById(mappingId).orElseThrow();

        if (!isMappingMatched(csvFile, mapping)) {
            int columnCount = getColumnCount(csvFile);
            return new CsvPreviewResult(new ArrayList<>(), true, columnCount);
        }

        return checkCsvWithMapping(csvFile, mapping);
    }

    public CsvPreviewResult checkCsvWithMapping(MultipartFile csvFile, MappingEntity mapping) throws IOException {
        List<CsvPreviewResponse> previewRows = new ArrayList<>();

        InputStream inputStream = csvFile.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CSVParser parser = CSVFormat.DEFAULT.parse(reader);

        int rowNumber = 1;
        boolean firstRow = true;

        for (CSVRecord record : parser) {
            if (firstRow) {
                if (hasHeader(record, mapping)) {
                    firstRow = false;
                    rowNumber++;
                    continue;
                }
                firstRow = false;
            }

            int columnCount = record.size();
            String errorMessage = null;

            LocalDate saleDate = null;
            int dateColumn = mapping.getDateColumn();

            if (dateColumn >= columnCount) {
                errorMessage = addError(errorMessage, "日付列がありません");
            } else {
                try {
                    saleDate = LocalDate.parse(record.get(dateColumn));
                } catch (Exception e) {
                    errorMessage = addError(errorMessage, "日付が正しくありません");
                }
            }

            Long amount = null;
            int amountColumn = mapping.getAmountColumn();

            if (amountColumn >= columnCount) {
                errorMessage = addError(errorMessage, "金額列がありません");
            } else {
                try {
                    amount = Long.valueOf(record.get(amountColumn));
                } catch (Exception e) {
                    errorMessage = addError(errorMessage, "売上金額が正しくありません");
                }
            }

            Integer customerColumn = mapping.getCustomerColumn();
            Integer customerCount = null;

            if (customerColumn != null) {
                if (customerColumn >= columnCount) {
                    errorMessage = addError(errorMessage, "客数列がありません");
                } else {
                    String customer = record.get(customerColumn);

                    if (customer != null) {
                        try {
                            customerCount = Integer.valueOf(customer);
                        } catch (Exception e) {
                            errorMessage = addError(errorMessage, "客数が正しくありません");
                        }
                    }
                }
            }

            Integer categoryColumn = mapping.getCategoryColumn();
            String category = null;

            if (categoryColumn != null) {
                if (categoryColumn >= columnCount) {
                    errorMessage = addError(errorMessage, "カテゴリ列がありません");
                } else {
                    category = record.get(categoryColumn);
                }
            }

            Integer colorColumn = mapping.getColorColumn();
            String color = null;

            if (colorColumn != null) {
                if (colorColumn >= columnCount) {
                    errorMessage = addError(errorMessage, "カラー列がありません");
                } else {
                    color = record.get(colorColumn);
                }
            }

            Integer quantityColumn = mapping.getQuantityColumn();
            Integer quantityValue = null;

            if (quantityColumn != null) {
                if (quantityColumn >= columnCount) {
                    errorMessage = addError(errorMessage, "数量列がありません");
                } else {
                    String quantity = record.get(quantityColumn);

                    try {
                        quantityValue = Integer.valueOf(quantity);
                    } catch (Exception e) {
                        errorMessage = addError(errorMessage, "数量が正しくありません");
                    }
                }
            }

            Integer subtotalColumn = mapping.getSubtotalColumn();
            Long subtotalValue = null;

            if (subtotalColumn != null) {
                if (subtotalColumn >= columnCount) {
                    errorMessage = addError(errorMessage, "小計列がありません");
                } else {
                    String subtotal = record.get(subtotalColumn);

                    try {
                        subtotalValue = Long.valueOf(subtotal);
                    } catch (Exception e) {
                        errorMessage = addError(errorMessage, "小計が正しくありません");
                    }
                }
            }

            CsvPreviewResponse previewRow = new CsvPreviewResponse(
                    rowNumber,
                    saleDate,
                    amount,
                    customerCount,
                    category,
                    color,
                    quantityValue,
                    subtotalValue,
                    errorMessage
            );

            previewRows.add(previewRow);
            rowNumber++;
        }

        return new CsvPreviewResult(previewRows, false, 0);
    }

    public void importCsv(List<CsvPreviewResponse> previewRows, String storeCode) {
        StoreEntity store = storeRepository.findByStoreCode(storeCode).orElseThrow();

        for (CsvPreviewResponse row : previewRows) {
            SaleEntity sale = saleService.getOrCreateSale(store.getId(), row.getSaleDate());

            sale.setAmount(row.getAmount());
            sale.setCustomerCount(row.getCustomerCount());
            sale.setUpdatedAt(LocalDateTime.now());
            saleRepository.save(sale);

            if (row.getCategory() != null && !row.getCategory().isBlank()) {
                ItemEntity item = new ItemEntity();
                item.setSaleId(sale.getId());
                item.setCategoryName(row.getCategory());
                item.setColorName(row.getColor());
                item.setQuantity(row.getQuantity() != null ? row.getQuantity() : 0);
                item.setSubtotal(row.getSubtotal() != null ? row.getSubtotal() : 0L);
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                itemRepository.save(item);
            }
        }
    }

    public MappingEntity createMappingEntity(List<String> mappingColumns) {
        MappingEntity mapping = new MappingEntity();
        mapping.setName("CSV自動マッピング");

        for (int i = 0; i < mappingColumns.size(); i++) {
            String mappingColumn = mappingColumns.get(i);

            switch (mappingColumn) {
                case "date":
                    mapping.setDateColumn(i);
                    break;
                case "amount":
                    mapping.setAmountColumn(i);
                    break;
                case "customerCount":
                    mapping.setCustomerColumn(i);
                    break;
                case "category":
                    mapping.setCategoryColumn(i);
                    break;
                case "color":
                    mapping.setColorColumn(i);
                    break;
                case "quantity":
                    mapping.setQuantityColumn(i);
                    break;
                case "subtotal":
                    mapping.setSubtotalColumn(i);
                    break;
                case "skip":
                    break;
            }
        }

        return mapping;
    }

    public MappingEntity createMapping(List<String> mappingColumns, String storeCode) {
        StoreEntity store = storeRepository.findByStoreCode(storeCode).orElseThrow();

        MappingEntity mapping = createMappingEntity(mappingColumns);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        MappingEntity savedMapping = mappingRepository.save(mapping);
        store.setMappingId(savedMapping.getId());
        storeRepository.save(store);

        return savedMapping;
    }
}