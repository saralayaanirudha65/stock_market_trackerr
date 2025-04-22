package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;

public class CompanyOverviewResponse {
    @SerializedName("Symbol")
    private String symbol;

    @SerializedName("Name")
    private String name;

    @SerializedName("Description")
    private String description;

    @SerializedName("PERatio")
    private String peRatio;

    @SerializedName("MarketCapitalization")
    private String marketCapitalization;

    @SerializedName("Sector")
    private String sector;

    @SerializedName("Industry")
    private String industry;

    // Getters
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPeRatio() { return peRatio; }
    public String getMarketCapitalization() { return marketCapitalization; }
    public String getSector() { return sector; }
    public String getIndustry() { return industry; }
}
