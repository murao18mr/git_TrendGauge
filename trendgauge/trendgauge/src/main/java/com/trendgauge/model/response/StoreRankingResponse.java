package com.trendgauge.model.response;

import java.math.BigDecimal;

public class StoreRankingResponse {
    private Long storeId;
    private String storeName;
    private BigDecimal ratio;

    public StoreRankingResponse(Long storeId, String storeName, BigDecimal ratio) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.ratio = ratio;
    }

    public Long getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public BigDecimal getRatio() {
        return ratio;
    }
}

