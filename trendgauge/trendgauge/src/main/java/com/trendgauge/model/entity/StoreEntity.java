package com.trendgauge.model.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "stores")
public class StoreEntity {
    public static final String TABLE_NAME = "stores";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "mapping_id")
    private Long mappingId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_code")
    private String storeCode;

    private String status;

    @Column(name = "manager_pin")
    private String managerPin;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    public StoreEntity() {
    }

    public StoreEntity(Long id, Long companyId, Long mappingId, String storeName, String storeCode, String status, String managerPin, LocalTime openingTime, LocalTime closingTime) {
        this.id = id;
        this.companyId = companyId;
        this.mappingId = mappingId;
        this.storeName = storeName;
        this.storeCode = storeCode;
        this.status = status;
        this.managerPin = managerPin;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerPin() {
        return managerPin;
    }

    public void setManagerPin(String managerPin) {
        this.managerPin = managerPin;
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
