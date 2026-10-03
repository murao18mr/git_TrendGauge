package com.trendgauge.model.request;

import java.time.LocalTime;

public class AdminStoreEditInput {
    private Long storeId;
    private String storeCode;
    private String storeName;
    private String email;
    private String status;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public AdminStoreEditInput() {
    }

    public AdminStoreEditInput(Long storeId, String storeCode, String storeName, String email, String status, LocalTime openingTime, LocalTime closingTime) {
        this.storeId = storeId;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.email = email;
        this.status = status;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
}
