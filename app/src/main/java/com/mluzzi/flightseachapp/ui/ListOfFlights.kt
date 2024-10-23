package com.mluzzi.flightseachapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

//@Composable
//fun ListOfFlights(
//    viewModel: ListOfFlightsViewModel = viewModel(factory = ListOfFlightsViewModel.factory)
//) {
//
//    val flightItems by viewModel.flightItems.collectAsState()
//
//    LazyColumn {
//        items(flightItems) { flightItem ->
//            FlightItem(flightItem) // Your FlightItem composable
//        }
//    }
//}