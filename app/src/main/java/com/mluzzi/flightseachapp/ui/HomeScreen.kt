package com.mluzzi.flightseachapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mluzzi.flightseachapp.R
import com.mluzzi.flightseachapp.data.model.Airport
import com.mluzzi.flightseachapp.data.model.Favorite

// HomeScreen composable function, entry point for the screen
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for experimental Material3 APIs
@Composable
fun HomeScreen(
    viewModel: FlightsViewModel = viewModel(factory = FlightsViewModel.factory) // Inject FlightsViewModel
) {
    // Collect state values from the ViewModel using collectAsState
    val searchText = viewModel.searchText.collectAsState().value
    val focusManager = LocalFocusManager.current // Get the focus manager
    val airportSuggestions = viewModel.flightsSuggestions.collectAsState().value
    val flightsResult = viewModel.flightsResult.collectAsState().value
    val selectedAirport = viewModel.selectedAirport.collectAsState().value
    var showSuggestions by remember { mutableStateOf(true) } // State to control suggestion visibility
    val favoriteFlights = viewModel.favoriteFlights.collectAsState(initial = emptyList()).value
    // Derived state to show favorites when search text is empty
    val showFavorites by remember(searchText, favoriteFlights) {
        derivedStateOf { searchText.isEmpty() }
    }

    // Scaffold provides the basic screen structure
    Scaffold(
        topBar = {
            // TopAppBar for the screen title
            TopAppBar(
                title = { Text(stringResource(R.string.flight_search_app)) },
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
            )
        }
    ) { innerPadding -> // Content padding provided by Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // OutlinedTextField for search input
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.onSearchTextChange(it) }, // Update search text in ViewModel
                label = { Text(stringResource(R.string.search_flight)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            showSuggestions = true // Show suggestions when focused
                        }
                    },
                shape = RoundedCornerShape(32.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        focusManager.clearFocus() // Clear focus on "Go" action
                    }
                ),
                singleLine = true
            )

            // Display favorites when search text is empty
            key(showFavorites, favoriteFlights) {
                if (searchText.isEmpty()) {
                    LoadFavorites(favoriteFlights, viewModel)
                }
            }

            // Display search suggestions when search text is not empty and suggestions are visible
            if (searchText.isNotEmpty() && showSuggestions) {
                LoadSearchSuggestions(
                    flightsSuggestions = airportSuggestions,
                    selectedAirport = { viewModel.selectAirport(it) }, // Update selected airport in ViewModel
                    seachText = { selectedAirport?.iataCode?.let { viewModel.onSearchTextChange(it) } }, // Update search text with selected airport
                    onClimChange = { showSuggestions = false } // Hide suggestions on click
                )
            }

            // Display flights from selected airport if one is selected
            if (selectedAirport != null) {
                key(selectedAirport.iataCode) {
                    LoadFlightsFromSelectedAirport(selectedAirport, flightsResult, viewModel)
                }
            }
        }
    }
}

// Composable function to display favorite flights
@Composable
fun LoadFavorites(
    favoriteFlights: List<Favorite>,
    viewModel: FlightsViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(favoriteFlights.size) { favorite ->
            var departAirport by remember { mutableStateOf<Airport?>(null) }
            var arriveAirport by remember { mutableStateOf<Airport?>(null) }

            // LaunchedEffect to fetch airport details for each favorite flight
            LaunchedEffect(key1 = favorite) {
                departAirport =
                    viewModel.getAirportByIataCode(favoriteFlights[favorite].departureCode)
                arriveAirport =
                    viewModel.getAirportByIataCode(favoriteFlights[favorite].destinationCode)
            }

            // Display FlightItem if airport details are available
            if (departAirport != null && arriveAirport != null) {
                FlightItem(
                    departAirport = departAirport!!,
                    arriveAirport = arriveAirport!!,
                    viewModel = viewModel(factory = FlightsViewModel.factory)
                )
            }
        }
    }
}

// Composable function to display search suggestions
@Composable
fun LoadSearchSuggestions(
    flightsSuggestions: List<Airport>,
    seachText: () -> Unit,
    selectedAirport: (Airport) -> Unit,
    onClimChange: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(flightsSuggestions.size) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        selectedAirport(flightsSuggestions[airport]) // Update selected airport
                        seachText() // Update search text
                        focusManager.clearFocus() // Clear focus
                        onClimChange() // Hide suggestions
                    },
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = flightsSuggestions[airport].iataCode,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = flightsSuggestions[airport].name)
            }
        }
    }
}

// Composable function to display flights from selected airport
@Composable
fun LoadFlightsFromSelectedAirport(
    selectedAirport: Airport,
    flightResult: List<Airport>,
    viewModel: FlightsViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(flightResult.size) { flight ->
            FlightItem(
                departAirport = selectedAirport,
                arriveAirport = flightResult[flight],
                viewModel = viewModel
            )
        }
    }
}
