package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;

public class StockQuote {
    @SerializedName("01. symbol")
    private String symbol;

    @SerializedName("02. open")
    private String open;

    @SerializedName("03. high")
    private String high;

    @SerializedName("04. low")
    private String low;

    @SerializedName("05. price")
    private String price;

    @SerializedName("07. latest trading day")
    private String latestTradingDay;

    @SerializedName("08. previous close")
    private String previousClose;

    @SerializedName("09. change")
    private String change;

    @SerializedName("10. change percent")
    private String changePercent;

    // Getters
    public String getSymbol() { return symbol; }
    public String getOpen() { return open; }
    public String getHigh() { return high; }
    public String getLow() { return low; }
    public String getPrice() { return price; }
    public String getLatestTradingDay() { return latestTradingDay; }
    public String getPreviousClose() { return previousClose; }
    public String getChange() { return change; }
    public String getChangePercent() { return changePercent; }
}
