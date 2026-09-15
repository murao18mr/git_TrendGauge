package com.trendgauge.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MemoInput {
    @NotNull(message = "日付は必須入力です")
    private LocalDate saleDate;

    @NotBlank(message = "メモの内容を入力してください")

    private String comment;

    public MemoInput() {
    }

    public MemoInput(LocalDate saleDate, String comment) {
        this.saleDate = saleDate;
        this.comment = comment;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
