package com.trendgauge.model.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ManagerPinInput {

    @NotBlank(message = "現在のPINコードを入力してください")
    @Pattern(regexp = "\\d{4}", message = "PINコードは4桁の数字で入力してください")
    private String pin;

    @NotBlank(message = "新しいPINコードを入力してください")
    @Pattern(regexp = "\\d{4}", message = "PINコードは4桁の数字で入力してください")
    private String newPin;

    @NotBlank(message = "確認用PINコードを入力してください")
    private String confirmPin;

    @AssertTrue(message = "新しいPINコードが一致しません")
    public boolean isPinMatching() {
        if (newPin == null || newPin.isBlank() || confirmPin == null || confirmPin.isBlank()) {
            return true;
        }
        return newPin.equals(confirmPin);
    }

    public ManagerPinInput() {
    }

    public ManagerPinInput(String pin, String newPin, String confirmPin) {
        this.pin = pin;
        this.newPin = newPin;
        this.confirmPin = confirmPin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public String getConfirmPin() {
        return confirmPin;
    }

    public void setConfirmPin(String confirmPin) {
        this.confirmPin = confirmPin;
    }
}
