package com.trendgauge.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminPosSettingInput {
    @NotNull(message = "対象店舗を選択してください")
    private Long storeId;

    @NotBlank(message = "連携先POSレジを選択してください")
    private String posType;

    @NotBlank(message = "契約者IDは必須入力です")
    @Size(max = 255, message = "契約者IDは255文字以内で入力してください")
    private String contractId;

    @NotBlank(message = "クライアントIDは必須入力です")
    @Size(max = 255, message = "クライアントIDは255文字以内で入力してください")
    private String clientId;

    private String clientSecret;

    @Size(max = 20, message = "スマレジ店舗IDは20文字以内で入力してください")
    private String smaregiStoreId;

    private boolean autoSync;

    @NotBlank(message = "同期時刻は必須入力です")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "同期時刻を正しく入力してください"
    )
    private String syncTime = "02:00";

    public AdminPosSettingInput() {
    }

    public AdminPosSettingInput(Long storeId, String posType, String contractId, String clientId, String clientSecret, String smaregiStoreId, boolean autoSync, String syncTime) {
        this.storeId = storeId;
        this.posType = posType;
        this.contractId = contractId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.smaregiStoreId = smaregiStoreId;
        this.autoSync = autoSync;
        this.syncTime = syncTime;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getPosType() {
        return posType;
    }

    public void setPosType(String posType) {
        this.posType = posType;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getSmaregiStoreId() {
        return smaregiStoreId;
    }

    public void setSmaregiStoreId(String smaregiStoreId) {
        this.smaregiStoreId = smaregiStoreId;
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }
}
