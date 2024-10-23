package com.mluzzi.flightseachapp.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByIataCode(iataCode: String): Airport?

    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%'")
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :query || '%'")
    fun searchAirportsByIataCode(query: String): Flow<List<Airport>>

}