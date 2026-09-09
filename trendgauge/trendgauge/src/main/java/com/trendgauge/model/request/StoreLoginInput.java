package com.trendgauge.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StoreLoginInput {
    @NotBlank(message = "店舗コードは必須入力です")
    @Size(max = 20, message = "店舗コードは20文字以内です")
    @Pattern(regexp = "^$|^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
    private String storeCode;

    @NotBlank(message = "パスワードは必須入力です")
    private String password;

    public StoreLoginInput() {
    }

    public StoreLoginInput(String storeCode, String password) {
        this.storeCode = storeCode;
        this.password = password;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
