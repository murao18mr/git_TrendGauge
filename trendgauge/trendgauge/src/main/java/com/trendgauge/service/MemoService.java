package com.trendgauge.service;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.repository.MemoRepository;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService;

    public MemoService(MemoRepository memoRepository, SaleRepository saleRepository, SaleService saleService){
        this.memoRepository = memoRepository;
        this.saleRepository = saleRepository;
        this.saleService = saleService;
    }

    @Transactional
    public void registerMemo(Long storeId, LocalDate saleDate, String comment){
        SaleEntity sale = saleService.getOrCreateSale(storeId, saleDate);

        MemoEntity memo = new MemoEntity();
        memo.setSaleId(sale.getId());
        memo.setComment(comment);
        memo.setCreatedAt(LocalDateTime.now());
        memo.setUpdatedAt(LocalDateTime.now());
        memoRepository.save(memo);
    }

    public List<MemoEntity> getTodayMemos(Long storeId, LocalDate saleDate){
        Optional<SaleEntity> sale = saleRepository.findByStoreIdAndSaleDate(storeId, saleDate);
        if(sale.isEmpty()){
            return List.of();
        }
        return memoRepository.findBySaleIdOrderByCreatedAtAsc(sale.get().getId());
    }

    public List<MemoEntity> getMemos(Long saleId){
        return memoRepository.findBySaleIdOrderByCreatedAtAsc(saleId);
    }

}
