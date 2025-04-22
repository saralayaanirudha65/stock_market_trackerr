package com.example.myapplication1;

public class StockInfo {

    private String symbol;
    private String name;
    private double price;
    private double peRatio;

    public StockInfo(String symbol, String name, double price, double peRatio) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.peRatio = peRatio;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }
}
