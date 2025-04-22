package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;

public class GlobalQuoteResponse {
    @SerializedName("Global Quote")
    private StockQuote globalQuote;

    public StockQuote getGlobalQuote() {
        return globalQuote;
    }
}
