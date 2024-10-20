package com.mluzzi.flightseachapp

import android.app.Application
import com.mluzzi.flightseachapp.data.AppDatabase

class ApplicationApp: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}