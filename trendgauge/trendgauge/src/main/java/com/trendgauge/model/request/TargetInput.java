package com.trendgauge.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TargetInput {
    @NotBlank(message = "年月を選択してください")
    private String targetMonth;

    @NotNull(message = "月間予算を入力してください")
    @Min(value = 0, message = "月間予算は0以上で入力してください")
    private Long targetAmount;

    private BigDecimal targetRatio;

    public TargetInput() {
    }

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
