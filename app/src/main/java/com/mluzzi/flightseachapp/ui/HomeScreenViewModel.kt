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
import com.mluzzi.flightseachapp.data.AirportDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(private val airportDao: AirportDao) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    val flightsSuggestions: StateFlow<List<Airport>> = searchText
        .debounce(300) // Adiciona um atraso para evitar pesquisas frequentes
        .distinctUntilChanged() // Impede pesquisas repetidas
        .flatMapLatest { query ->
            airportDao.searchFlights(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ApplicationApp)
                HomeScreenViewModel(application.database.flightDao())
            }
        }
    }
}