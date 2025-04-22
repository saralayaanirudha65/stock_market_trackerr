package com.example.myapplication1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlphaVantageApi {
    @GET("query")
    Call<GlobalQuoteResponse> getStockQuote(
            @Query("function") String function,
            @Query("symbol") String symbol,
            @Query("apikey") String apiKey
    );

    @GET("query")
    Call<TimeSeriesResponse> getTimeSeriesDaily(
            @Query("function") String function,
            @Query("symbol") String symbol,
            @Query("outputsize") String outputSize,
            @Query("apikey") String apiKey
    );

    @GET("query")
    Call<CompanyOverviewResponse> getCompanyOverview(
            @Query("function") String function,
            @Query("symbol") String symbol,
            @Query("apikey") String apiKey
    );
}
