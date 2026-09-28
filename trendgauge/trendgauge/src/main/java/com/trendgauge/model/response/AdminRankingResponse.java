package com.trendgauge.model.response;

import java.math.BigDecimal;

public class AdminRankingResponse {
    private Long storeId;
    private String storeName;
    private Long sales;
    private Long targetAmount;
    private BigDecimal budgetRatio;
    private BigDecimal ratio;

    public AdminRankingResponse(Long storeId, String storeName, Long sales, Long targetAmount, BigDecimal budgetRatio, BigDecimal ratio) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.sales = sales;
        this.targetAmount = targetAmount;
        this.budgetRatio = budgetRatio;
        this.ratio = ratio;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getBudgetRatio() {
        return budgetRatio;
    }

    public void setBudgetRatio(BigDecimal budgetRatio) {
        this.budgetRatio = budgetRatio;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }
}
