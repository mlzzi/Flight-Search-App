package com.mluzzi.flightseachapp.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport")
    fun getAllFights(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE name LIKE :query || '%'")
    fun searchFlights(query: String): Flow<List<Airport>>
}