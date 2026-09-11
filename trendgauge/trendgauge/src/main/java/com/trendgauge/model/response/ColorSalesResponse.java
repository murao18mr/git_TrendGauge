package com.trendgauge.model.response;

public class ColorSalesResponse {
    private String colorName;
    private Long amount;

    public ColorSalesResponse(String colorName, Long amount) {
        this.colorName = colorName;
        this.amount = amount;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
