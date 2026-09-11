package com.trendgauge.service;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.repository.MemoRepository;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final SaleRepository saleRepository;

    public MemoService(MemoRepository memoRepository, SaleRepository saleRepository){
        this.memoRepository = memoRepository;
        this.saleRepository = saleRepository;
    }

    public SaleEntity getOrCreateSale(Long storeId, LocalDate saleDate){
        Optional<SaleEntity> existingSale = saleRepository.findByStoreIdAndSaleDate(storeId, saleDate);
        if(existingSale.isEmpty()){
            SaleEntity newSale = new SaleEntity();
            newSale.setStoreId(storeId);
            newSale.setSaleDate(saleDate);
            SaleEntity savedSale = saleRepository.save(newSale);
            return savedSale;
        }
        return existingSale.get();
    }

    public void registerMemo(Long storeId, LocalDate saleDate, String comment){
        SaleEntity sale = getOrCreateSale(storeId, saleDate);

        MemoEntity memo = new MemoEntity();
        memo.setSaleId(sale.getId());
        memo.setComment(comment);
        memoRepository.save(memo);
    }

    public List<MemoEntity> getTodayMemos(Long storeId, LocalDate saleDate){
        Optional<SaleEntity> sale = saleRepository.findByStoreIdAndSaleDate(storeId, saleDate);
        if(sale.isEmpty()){
            return List.of();
        }
        return memoRepository.findBySaleIdOrderByCreatedAtAsc(sale.get().getId());
    }

}
