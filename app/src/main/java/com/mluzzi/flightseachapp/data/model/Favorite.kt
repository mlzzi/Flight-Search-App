package com.mluzzi.flightseachapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "departure_code")
    var departureCode: String,
    @ColumnInfo(name = "destination_code")
    var destinationCode: String
)