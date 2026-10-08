package com.trendgauge.model.response;

public class AdminPosSettingResponse {
    private boolean registered;
    private String posType;
    private String contractId;
    private String clientId;
    private String smaregiStoreId;
    private boolean autoSync;
    private String syncTime;

    public AdminPosSettingResponse(boolean registered, String posType, String contractId, String clientId, String smaregiStoreId, boolean autoSync, String syncTime) {
        this.registered = registered;
        this.posType = posType;
        this.contractId = contractId;
        this.clientId = clientId;
        this.smaregiStoreId = smaregiStoreId;
        this.autoSync = autoSync;
        this.syncTime = syncTime;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
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
