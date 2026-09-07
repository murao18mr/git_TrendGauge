package com.trendgauge.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mappings")
public class MappingEntity {
    public static final String TABLE_NAME = "mappings";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "date_column")
    private int dateColumn;

    @Column(name = "amount_column")
    private int amountColumn;

    @Column(name = "customer_column")
    private Integer customerColumn;

    @Column(name = "category_column")
    private Integer categoryColumn;

    @Column(name = "color_column")
    private Integer colorColumn;

    @Column(name = "quantity_column")
    private Integer quantityColumn;

    @Column(name = "subtotal_column")
    private Integer subtotalColumn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MappingEntity() {
    }

    public MappingEntity(Long id, String name, int dateColumn, int amountColumn, Integer customerColumn, Integer categoryColumn, Integer colorColumn, Integer quantityColumn, Integer subtotalColumn, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.dateColumn = dateColumn;
        this.amountColumn = amountColumn;
        this.customerColumn = customerColumn;
        this.categoryColumn = categoryColumn;
        this.colorColumn = colorColumn;
        this.quantityColumn = quantityColumn;
        this.subtotalColumn = subtotalColumn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDateColumn() {
        return dateColumn;
    }

    public void setDateColumn(int dateColumn) {
        this.dateColumn = dateColumn;
    }

    public int getAmountColumn() {
        return amountColumn;
    }

    public void setAmountColumn(int amountColumn) {
        this.amountColumn = amountColumn;
    }

    public Integer getCustomerColumn() {
        return customerColumn;
    }

    public void setCustomerColumn(Integer customerColumn) {
        this.customerColumn = customerColumn;
    }

    public Integer getCategoryColumn() {
        return categoryColumn;
    }

    public void setCategoryColumn(Integer categoryColumn) {
        this.categoryColumn = categoryColumn;
    }

    public Integer getColorColumn() {
        return colorColumn;
    }

    public void setColorColumn(Integer colorColumn) {
        this.colorColumn = colorColumn;
    }

    public Integer getQuantityColumn() {
        return quantityColumn;
    }

    public void setQuantityColumn(Integer quantityColumn) {
        this.quantityColumn = quantityColumn;
    }

    public Integer getSubtotalColumn() {
        return subtotalColumn;
    }

    public void setSubtotalColumn(Integer subtotalColumn) {
        this.subtotalColumn = subtotalColumn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
