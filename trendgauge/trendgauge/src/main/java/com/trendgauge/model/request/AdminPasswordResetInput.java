package com.trendgauge.model.request;

public class AdminPasswordResetInput {
    private Long storeId;
    private String password;
    private String confirmPassword;

    public AdminPasswordResetInput() {
    }

    public AdminPasswordResetInput(Long storeId, String password, String confirmPassword) {
        this.storeId = storeId;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
