package com.mluzzi.flightseachapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mluzzi.flightseachapp.data.dao.AirportDao
import com.mluzzi.flightseachapp.data.dao.FavoriteDao
import com.mluzzi.flightseachapp.data.model.Airport
import com.mluzzi.flightseachapp.data.model.Favorite

@Database(entities = [Airport::class, Favorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "flight_search"
                )
                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/flight_search.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}