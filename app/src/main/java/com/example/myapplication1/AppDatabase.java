package com.example.myapplication1;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WatchlistStock.class, HoldingStock.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract WatchlistDao watchlistDao();
    public abstract HoldingsDao holdingsDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "stock_db")
                    .fallbackToDestructiveMigration() // Wipes DB if schema changed
                    .build();
        }
        return instance;
    }
}
