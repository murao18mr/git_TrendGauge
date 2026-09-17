package com.trendgauge.model.response;

import java.time.LocalDate;

public class CsvPreviewResponse {
    private int rowNumber;
    private LocalDate saleDate;
    private Long amount;
    private Integer customerCount;
    private String category;
    private String color;
    private Integer quantity;
    private Long subtotal;
    private String errorMessage;

    public CsvPreviewResponse(int rowNumber, LocalDate saleDate, Long amount, Integer customerCount, String category, String color, Integer quantity, Long subtotal, String errorMessage) {
        this.rowNumber = rowNumber;
        this.saleDate = saleDate;
        this.amount = amount;
        this.customerCount = customerCount;
        this.category = category;
        this.color = color;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.errorMessage = errorMessage;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
