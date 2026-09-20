package com.trendgauge.model.request;

import java.util.List;

public class SalesEditInput {
    private Long saleId;
    private Long amount;
    private Integer customerCount;
    private String weather;
    private String report;
    private List<MemoEditInput> memos;

    public SalesEditInput(Long saleId, Long amount, Integer customerCount, String weather, String report, List<MemoEditInput> memos) {
        this.saleId = saleId;
        this.amount = amount;
        this.customerCount = customerCount;
        this.weather = weather;
        this.report = report;
        this.memos = memos;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public List<MemoEditInput> getMemos() {
        return memos;
    }

    public void setMemos(List<MemoEditInput> memos) {
        this.memos = memos;
    }
}
