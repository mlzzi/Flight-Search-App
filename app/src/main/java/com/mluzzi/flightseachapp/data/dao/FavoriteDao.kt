package com.mluzzi.flightseachapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mluzzi.flightseachapp.data.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT id FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun getFavoriteId(departureCode: String, destinationCode: String): Int

    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT * FROM favorite")
    fun getAllFavorite(): Flow<List<Favorite>>

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode)")
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean


}