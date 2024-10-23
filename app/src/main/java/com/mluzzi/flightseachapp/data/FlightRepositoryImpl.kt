package com.mluzzi.flightseachapp.data

import kotlinx.coroutines.flow.Flow

class FlightRepositoryImpl(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) : FlightRepository {

    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    override fun getAirportByIataCode(iataCode: String): Airport? = airportDao.getAirportByIataCode(iataCode)

    override fun searchAirports(query: String): Flow<List<Airport>> = airportDao.searchAirports(query)

    override fun searchAirportsByIataCode(query: String): Flow<List<Airport>> = airportDao.searchAirportsByIataCode(query)

    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorite()

    override fun insertFavorite(favorite: Favorite) {
        val favorite = Favorite(
            id = favorite.id,
            departureCode = favorite.departureCode,
            destinationCode = favorite.destinationCode
        )
        favoriteDao.insertFavorite(favorite)
    }

    override fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavorite(favorite)

}