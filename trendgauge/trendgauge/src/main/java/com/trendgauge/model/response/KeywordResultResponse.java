package com.trendgauge.model.response;

import java.time.LocalDate;

public class KeywordResultResponse {
    private LocalDate saleDate;
    private Long amount;
    private String weather;
    private String text;

    public KeywordResultResponse(LocalDate saleDate, Long amount, String weather, String text) {
        this.saleDate = saleDate;
        this.amount = amount;
        this.weather = weather;
        this.text = text;
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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
