package com.trendgauge.model.response;

public class CategorySalesResponse {

    private String categoryName;
    private Long amount;

    public CategorySalesResponse(String categoryName, Long amount) {
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}