package com.example.myapplication1;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("news")
    Call<List<NewsItem>> getStockNews(
            @Query("s") String symbol,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("api_token") String apiKey
    );

    @GET("news")
    Call<List<NewsItem>> getMarketNews(
            @Query("t") String tag,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("api_token") String apiKey
    );


}
