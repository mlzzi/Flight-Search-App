@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.mluzzi.flightseachapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mluzzi.flightseachapp.ApplicationApp
import com.mluzzi.flightseachapp.data.Airport
import com.mluzzi.flightseachapp.data.Favorite
import com.mluzzi.flightseachapp.data.FlightRepositoryImpl
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
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    val flightsSuggestions: StateFlow<List<Airport>> = searchText
        .debounce(300) // Adiciona um atraso para evitar pesquisas frequentes
        .distinctUntilChanged() // Impede pesquisas repetidas
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

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    val flightsResult: StateFlow<List<Airport>> = combine(
        flightRepositoryImpl.getAllAirports(),
        selectedAirport
    ) { allAirports, selectedAirport ->
        allAirports.filter { it.iataCode != selectedAirport?.iataCode }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
    }

    suspend fun getFavoriteId(departureCode: String, destinationCode: String): Int {
        return withContext(Dispatchers.IO) {
            flightRepositoryImpl.getFavoriteId(departureCode, destinationCode)
        }
    }

    fun insertOrDeleteFavorite(departAirport: Airport, arriveAirport: Airport) {
        viewModelScope.launch {
            val isFavorite = flightRepositoryImpl.isFavorite(departAirport.iataCode, arriveAirport.iataCode)
            if (isFavorite) {
                val id = flightRepositoryImpl.getFavoriteId(departAirport.iataCode, arriveAirport.iataCode)
                flightRepositoryImpl.deleteFavoriteById(id)
            } else {
                flightRepositoryImpl.insertFavorite(
                    Favorite(
                        id = 0,  // ID será gerado automaticamente
                        departureCode = departAirport.iataCode,
                        destinationCode = arriveAirport.iataCode
                    )
                )
            }
        }
    }

    val favoriteFlights: Flow<List<Favorite>> = flightRepositoryImpl.getAllFavorites()

    fun isFavorite(departureCode: String, destinationCode: String): Boolean {
        var isFavorite = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (flightRepositoryImpl.isFavorite(departureCode, destinationCode)) {
                    isFavorite = true
                }
            }
        }
        return isFavorite
    }


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