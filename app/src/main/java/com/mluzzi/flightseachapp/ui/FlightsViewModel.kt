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

    val flights: Flow<List<Airport>> = flightRepositoryImpl.getAllAirports()

    fun insertFavorite(favorite: Favorite) {
        flightRepositoryImpl.insertFavorite(favorite)
    }

    fun deleteFavorite(favorite: Favorite) {
        flightRepositoryImpl.deleteFavorite(favorite)
    }

    val favoriteFlights: Flow<List<Favorite>> = flightRepositoryImpl.getAllFavorites()

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