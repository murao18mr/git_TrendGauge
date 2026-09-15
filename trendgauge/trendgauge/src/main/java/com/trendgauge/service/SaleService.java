package com.trendgauge.service;

import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SaleService {
    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository){
        this.saleRepository = saleRepository;
    }

    public SaleEntity getOrCreateSale(Long storeId, LocalDate saleDate){
        Optional<SaleEntity> existingSale = saleRepository.findByStoreIdAndSaleDate(storeId, saleDate);
        if(existingSale.isEmpty()){
            SaleEntity newSale = new SaleEntity();
            newSale.setStoreId(storeId);
            newSale.setSaleDate(saleDate);
            newSale.setCreatedAt(LocalDateTime.now());
            newSale.setUpdatedAt(LocalDateTime.now());
            SaleEntity savedSale = saleRepository.save(newSale);
            return savedSale;
        }
        return existingSale.get();
    }
}
