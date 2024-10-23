package com.mluzzi.flightseachapp.data

import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    fun getAllAirports(): Flow<List<Airport>>
    fun getAirportByIataCode(iataCode: String): Airport?
    fun searchAirports(query: String): Flow<List<Airport>>
    fun searchAirportsByIataCode(query: String): Flow<List<Airport>>
    fun getAllFavorites(): Flow<List<Favorite>>
    fun insertFavorite(favorite: Favorite)
    fun deleteFavorite(favorite: Favorite)
}