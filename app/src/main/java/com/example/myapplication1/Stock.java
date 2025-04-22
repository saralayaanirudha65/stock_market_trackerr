package com.example.myapplication1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchlist")
public class Stock {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String symbol;
    public String name;
    public double price;
    public double peRatio;

    public Stock(String symbol, String name, double price, double peRatio) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.peRatio = peRatio;
    }
}
