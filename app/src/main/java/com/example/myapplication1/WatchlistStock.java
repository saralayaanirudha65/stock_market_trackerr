package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchlist")
public class WatchlistStock {
    @PrimaryKey
    @NonNull
    private String symbol = "";
    private String name;
    private double price;
    private double peRatio;
    private String lastUpdated;
    private double changePercent;
    private String marketCap; // Added market cap field

    // No-arg constructor required by Room
    public WatchlistStock() {
    }

    public WatchlistStock(@NonNull String symbol, String name, double price, double peRatio) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.peRatio = peRatio;
        this.lastUpdated = java.time.LocalDateTime.now().toString();
        this.changePercent = 0.0;
        this.marketCap = "N/A"; // Default value for market cap
    }

    // Constructor with market cap parameter
    public WatchlistStock(@NonNull String symbol, String name, double price, double peRatio, String marketCap) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.peRatio = peRatio;
        this.lastUpdated = java.time.LocalDateTime.now().toString();
        this.changePercent = 0.0;
        this.marketCap = marketCap;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }
}
