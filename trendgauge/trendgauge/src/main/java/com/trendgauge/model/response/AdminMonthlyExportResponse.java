package com.trendgauge.model.response;

public class AdminMonthlyExportResponse {
    private String storeName;
    private String targetMonth;
    private Long totalAmount;
    private Integer totalCustomerCount;

    public AdminMonthlyExportResponse(String storeName, String targetMonth, Long totalAmount, Integer totalCustomerCount) {
        this.storeName = storeName;
        this.targetMonth = targetMonth;
        this.totalAmount = totalAmount;
        this.totalCustomerCount = totalCustomerCount;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTargetMonth() {
        return targetMonth;
    }

    public void setTargetMonth(String targetMonth) {
        this.targetMonth = targetMonth;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalCustomerCount() {
        return totalCustomerCount;
    }

    public void setTotalCustomerCount(Integer totalCustomerCount) {
        this.totalCustomerCount = totalCustomerCount;
    }
}
