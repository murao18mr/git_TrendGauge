package com.trendgauge.model.response;

import java.time.LocalDate;

public class SalesTrendResponse {
    private LocalDate date;
    private Long amount;

    public SalesTrendResponse(LocalDate date, Long amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
