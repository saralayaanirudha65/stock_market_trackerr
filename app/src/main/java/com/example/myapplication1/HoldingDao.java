package com.example.myapplication1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HoldingDao {
    @Insert
    void insert(HoldingStock stock);

    @Update
    void update(HoldingStock stock);

    @Delete
    void delete(HoldingStock stock);

    @Query("SELECT * FROM holdings")
    List<HoldingStock> getAllHoldings();

    @Query("SELECT * FROM holdings WHERE symbol = :symbol LIMIT 1")
    HoldingStock getHoldingBySymbol(String symbol);
}
