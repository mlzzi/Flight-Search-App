package com.mluzzi.flightseachapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "departure_code")
    var departureCode: String?,
    @ColumnInfo(name = "destination_code")
    var destinationCode: String?
)