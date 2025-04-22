package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "holdings")
public class HoldingStock {
    @PrimaryKey
    @NonNull
    private String symbol = "";
    private String name;
    private double pricePerStock;
    private int quantity;
    private String purchaseDate;

    // No-arg constructor required by Room
    public HoldingStock() {
    }

    public HoldingStock(@NonNull String symbol, String name, double pricePerStock, int quantity) {
        this.symbol = symbol;
        this.name = name;
        this.pricePerStock = pricePerStock;
        this.quantity = quantity;
        this.purchaseDate = java.time.LocalDateTime.now().toString();
    }

    // Getters and setters
    @NonNull
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NonNull String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerStock() {
        return pricePerStock;
    }

    public void setPricePerStock(double pricePerStock) {
        this.pricePerStock = pricePerStock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getTotalValue() {
        return pricePerStock * quantity;
    }
}
