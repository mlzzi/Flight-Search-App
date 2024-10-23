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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mluzzi.flightseachapp.data.Airport
import com.mluzzi.flightseachapp.ui.theme.FlightSeachAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FlightsViewModel = viewModel(factory = FlightsViewModel.factory)
) {
    val searchText = viewModel.searchText.collectAsState().value
    val focusManager = LocalFocusManager.current
    val airportSuggestions = viewModel.flightsSuggestions.collectAsState().value
    val flights = viewModel.flights.collectAsState(initial = emptyList()).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nome do App") },
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var selectedAirport by remember { mutableStateOf<Airport?>(null) }

            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.onSearchTextChange(it) },
                label = { Text("Pesquisar") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )

            if (searchText.isNotEmpty()) {
                LoadSearchSuggestions(
                    flightsSuggestions = airportSuggestions,
                    selectedAirport = { selectedAirport = it },
                    seachText = { viewModel.onSearchTextChange("") }
                )
            }

            selectedAirport?.let { LoadFlightsFromSelectedAirport(it, flights) }
        }
    }
}

@Composable
fun LoadSearchSuggestions(
    flightsSuggestions: List<Airport>,
    seachText: () -> Unit,
    selectedAirport: (Airport) -> Unit,
) {

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
                        selectedAirport(flightsSuggestions[airport])
                        seachText()
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

@Composable
fun LoadFlightsFromSelectedAirport(
    selectedAirport: Airport,
    flights: List<Airport>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(flights.size) { flight ->
            FlightItem(
                departAirport = selectedAirport,
                arriveAirport = flights[flight]
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadFlightsFromSelectedAirportPreview() {
    FlightSeachAppTheme {
        LoadFlightsFromSelectedAirport(
            selectedAirport = Airport(
                id = 1,
                iataCode = "OPO",
                name = "Francisco Sá Carneiro Airport",
                passengers = 10000000
            ),
            flights = listOf(
                Airport(
                    id = 2,
                    iataCode = "LIS",
                    name = "Humberto Delgado Airport",
                    passengers = 8000000
                ),
                Airport(
                    id = 3,
                    iataCode = "MAD",
                    name = "Adolfo Suárez Madrid–Barajas Airport",
                    passengers = 12000000
                )
            )
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}