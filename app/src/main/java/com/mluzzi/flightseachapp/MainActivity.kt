package com.mluzzi.flightseachapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.mluzzi.flightseachapp.data.AppDatabase
import com.mluzzi.flightseachapp.ui.HomeScreen
import com.mluzzi.flightseachapp.ui.theme.FlightSeachAppTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Access Database
        val database = AppDatabase.getDatabase(applicationContext)

        // Launch Coroutine
        lifecycleScope.launch {
            database.flightDao().getAllFights().collect { flights ->
                // Access the loaded data and use in your composables or viewmodels
                Log.d("FLIGHTS", flights.toString())
            }
        }
        setContent {
            FlightSeachAppTheme {
                HomeScreen()
            }
        }
    }
}