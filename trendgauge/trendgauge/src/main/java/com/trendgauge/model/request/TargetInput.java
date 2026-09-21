package com.trendgauge.model.request;

import java.math.BigDecimal;

public class TargetInput {
    private String targetMonth;
    private Long targetAmount;
    private BigDecimal targetRatio;

    public TargetInput(String targetMonth, Long targetAmount, BigDecimal targetRatio) {
        this.targetMonth = targetMonth;
        this.targetAmount = targetAmount;
        this.targetRatio = targetRatio;
    }

    public String getTargetMonth() {
        return targetMonth;
    }

    public void setTargetMonth(String targetMonth) {
        this.targetMonth = targetMonth;
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
