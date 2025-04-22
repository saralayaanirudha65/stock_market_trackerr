package com.example.myapplication1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStock(WatchlistStock stock);

    @Query("SELECT * FROM watchlist")
    List<WatchlistStock> getAllStocks();

    @Delete
    void deleteStock(WatchlistStock stock);

    @Update
    void updateStock(WatchlistStock stock);

    @Query("SELECT * FROM watchlist WHERE symbol = :symbol LIMIT 1")
    WatchlistStock getStockBySymbol(String symbol);
}
