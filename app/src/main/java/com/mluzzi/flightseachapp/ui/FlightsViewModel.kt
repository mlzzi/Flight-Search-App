@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.mluzzi.flightseachapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mluzzi.flightseachapp.ApplicationApp
import com.mluzzi.flightseachapp.data.model.Airport
import com.mluzzi.flightseachapp.data.model.Favorite
import com.mluzzi.flightseachapp.data.repository.FlightRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightsViewModel(private val flightRepositoryImpl: FlightRepositoryImpl) : ViewModel() {

    // StateFlow to hold the current search text
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // StateFlow to hold the list of airport suggestions based on the search text
    val flightsSuggestions: StateFlow<List<Airport>> = searchText
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            combine(
                flightRepositoryImpl.searchAirports(query),
                flightRepositoryImpl.searchAirportsByIataCode(query)
            ) { nameResults, iataResults ->
                (nameResults + iataResults).distinctBy { it.id }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Updates the search text and clears the selected airport if the search text is empty
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        if (text.isEmpty()) {
            _selectedAirport.value = null
        }
    }

    // StateFlow to hold the currently selected airport
    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    // StateFlow to hold the list of flight results based on the selected airport
    val flightsResult: StateFlow<List<Airport>> = combine(
        flightRepositoryImpl.getAllAirports(),
        selectedAirport
    ) { allAirports, selectedAirport ->
        allAirports.filterNot { it.iataCode == selectedAirport?.iataCode }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Updates the selected airport
    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
    }

    // Retrieves an airport by its IATA code using the repository
    suspend fun getAirportByIataCode(iataCode: String): Airport? =
        withContext(Dispatchers.IO) {
            flightRepositoryImpl.getAirportByIataCode(iataCode)
        }

    // Inserts or deletes a favorite flight based on whether it already exists
    fun insertOrDeleteFavorite(departAirport: Airport, arriveAirport: Airport) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite =
                flightRepositoryImpl.isFavorite(departAirport.iataCode, arriveAirport.iataCode)
            if (isFavorite) {
                val id = flightRepositoryImpl.getFavoriteId(
                    departAirport.iataCode,
                    arriveAirport.iataCode
                )
                flightRepositoryImpl.deleteFavoriteById(id)
            } else {
                flightRepositoryImpl.insertFavorite(
                    Favorite(
                        id = 0,
                        departureCode = departAirport.iataCode,
                        destinationCode = arriveAirport.iataCode
                    )
                )
            }
        }
    }

    // Flow to retrieve all favorite flights from the repository
    val favoriteFlights: Flow<List<Favorite>> = flightRepositoryImpl.getAllFavorites()

    // Companion object to provide a factory for creating instances of the ViewModel
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ApplicationApp)
                val flightRepositoryImpl = FlightRepositoryImpl(
                    application.database.airportDao(),
                    application.database.favoriteDao()
                )
                FlightsViewModel(flightRepositoryImpl)
            }
        }
    }
}