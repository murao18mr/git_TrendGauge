package com.trendgauge.model.response;

public class StoreAccountResponse {
    private Long storeId;
    private String storeCode;
    private String storeName;
    private String email;
    private String status;

    public StoreAccountResponse(Long storeId, String storeCode, String storeName, String email, String status) {
        this.storeId = storeId;
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.email = email;
        this.status = status;
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

    public String getStatusText() {
        if ("active".equals(status)) {
            return "営業中";
        }
        return "閉店";
    }
}
