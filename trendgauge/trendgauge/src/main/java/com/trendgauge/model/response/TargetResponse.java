package com.trendgauge.model.response;

import java.math.BigDecimal;

public class TargetResponse {
    private Long targetAmount;
    private BigDecimal targetRatio;

    public TargetResponse(Long targetAmount, BigDecimal targetRatio) {
        this.targetAmount = targetAmount;
        this.targetRatio = targetRatio;
    }

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getTargetRatio() {
        return targetRatio;
    }

    public void setTargetRatio(BigDecimal targetRatio) {
        this.targetRatio = targetRatio;
    }
}
