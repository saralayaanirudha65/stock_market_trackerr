package com.example.myapplication1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StockDao {

    @Insert
    void insert(Stock stock);

    @Delete
    void delete(Stock stock);

    @Query("SELECT * FROM watchlist")
    List<Stock> getAll();
}
