package com.mluzzi.flightseachapp.data.repository

import com.mluzzi.flightseachapp.data.dao.AirportDao
import com.mluzzi.flightseachapp.data.dao.FavoriteDao
import com.mluzzi.flightseachapp.data.model.Airport
import com.mluzzi.flightseachapp.data.model.Favorite
import kotlinx.coroutines.flow.Flow

class FlightRepositoryImpl(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) : FlightRepository {

    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    override fun getAirportByIataCode(iataCode: String): Airport? =
        airportDao.getAirportByIataCode(iataCode)

    override fun searchAirports(query: String): Flow<List<Airport>> =
        airportDao.searchAirports(query)

    override fun searchAirportsByIataCode(query: String): Flow<List<Airport>> =
        airportDao.searchAirportsByIataCode(query)


    override suspend fun getFavoriteId(departureCode: String, destinationCode: String): Int =
        favoriteDao.getFavoriteId(departureCode, destinationCode)

    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorite()

    override suspend fun insertFavorite(favorite: Favorite) {
        val favorite = Favorite(
            id = favorite.id,
            departureCode = favorite.departureCode,
            destinationCode = favorite.destinationCode
        )
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean =
        favoriteDao.isFavorite(departureCode, destinationCode)

    override suspend fun deleteFavoriteById(id: Int) {
        favoriteDao.deleteFavoriteById(id)
    }

}