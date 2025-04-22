package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class TimeSeriesResponse {
    @SerializedName("Time Series (Daily)")
    private Map<String, DailyData> timeSeriesDaily;

    public Map<String, DailyData> getTimeSeriesDaily() {
        return timeSeriesDaily;
    }

    public static class DailyData {
        @SerializedName("1. open")
        private String open;

        @SerializedName("4. close")
        private String close;

        public String getOpen() { return open; }
        public String getClose() { return close; }
    }
}
