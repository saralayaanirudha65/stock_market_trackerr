package com.example.myapplication1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HoldingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHolding(HoldingStock holding);

    @Query("SELECT * FROM holdings")
    List<HoldingStock> getAllHoldings();

    @Delete
    void deleteHolding(HoldingStock holding);

    @Update
    void updateHolding(HoldingStock holding);

    @Query("SELECT * FROM holdings WHERE symbol = :symbol LIMIT 1")
    HoldingStock getHoldingBySymbol(String symbol);
}
