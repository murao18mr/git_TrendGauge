package com.trendgauge.model.request;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public class AdminAccountInput {
    @NotBlank(message = "店舗名は必須入力です")
    private String storeName;

    @NotBlank(message = "店舗コードは必須入力です")
    @Size(max = 20, message = "店舗コードは20文字以内で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
    private String storeCode;

    @NotBlank(message = "店長PINは必須入力です")
    @Pattern(regexp = "^[0-9]{4}$", message = "4桁の数字で入力してください")
    private String managerPin;

    @NotBlank(message = "メールアドレスは必須入力です")
    @Email(message = "メールアドレスの形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須入力です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @NotBlank(message = "確認パスワードは必須です")
    private String confirmPassword;

    @AssertTrue(message = "パスワードと確認用パスワードが一致しません")
    public boolean isPasswordMatching(){
        if (password == null || password.isBlank() || confirmPassword == null || confirmPassword.isBlank()) {
            return true;
        }
        return password.equals(confirmPassword);
    }

    private LocalTime openingTime;
    private LocalTime closingTime;

    public AdminAccountInput() {
    }

    public AdminAccountInput(String storeName, String storeCode, String managerPin, String email, String password, String confirmPassword, LocalTime openingTime, LocalTime closingTime) {
        this.storeName = storeName;
        this.storeCode = storeCode;
        this.managerPin = managerPin;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getManagerPin() {
        return managerPin;
    }

    public void setManagerPin(String managerPin) {
        this.managerPin = managerPin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
