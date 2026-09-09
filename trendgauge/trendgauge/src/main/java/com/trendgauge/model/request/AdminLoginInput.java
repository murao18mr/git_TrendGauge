package com.trendgauge.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class AdminLoginInput {
    @NotBlank(message = "メールアドレスは必須入力です")
    @Email(message = "メールアドレスの形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須入力です")
    private String password;

    public AdminLoginInput() {
    }

    public AdminLoginInput(String email, String password) {
        this.email = email;
        this.password = password;
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
}
