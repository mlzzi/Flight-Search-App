package com.mluzzi.flightseachapp.data.repository

import com.mluzzi.flightseachapp.data.model.Airport
import com.mluzzi.flightseachapp.data.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    fun getAllAirports(): Flow<List<Airport>>
    fun getAirportByIataCode(iataCode: String): Airport?
    fun searchAirports(query: String): Flow<List<Airport>>
    fun searchAirportsByIataCode(query: String): Flow<List<Airport>>

    suspend fun getFavoriteId(departureCode: String, destinationCode: String): Int
    fun getAllFavorites(): Flow<List<Favorite>>
    suspend fun insertFavorite(favorite: Favorite)
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean
    suspend fun deleteFavoriteById(id: Int)
}