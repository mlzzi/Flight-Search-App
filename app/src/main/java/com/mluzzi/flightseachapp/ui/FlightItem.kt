package com.mluzzi.flightseachapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mluzzi.flightseachapp.R
import com.mluzzi.flightseachapp.data.Airport
import com.mluzzi.flightseachapp.data.AirportDao
import com.mluzzi.flightseachapp.data.Favorite
import com.mluzzi.flightseachapp.data.FlightRepositoryImpl

@Composable
fun FlightItem(
    departAirport: Airport,
    arriveAirport: Airport,
    modifier: Modifier = Modifier,
    viewModel: FlightsViewModel
) {
    val isFavorite = viewModel.favoriteFlights.collectAsState(initial = emptyList())
        .value.any { it.departureCode == departAirport.iataCode && it.destinationCode == arriveAirport.iataCode }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.flight_item))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.depart))
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        departAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        departAirport.name,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.arrive))
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        arriveAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        arriveAirport.name,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(R.string.favorite),
                modifier = Modifier.clickable {
                    viewModel.insertOrDeleteFavorite(departAirport, arriveAirport)
                },
                tint = if (isFavorite) colorResource(R.color.start_favorite) else Color.Gray
            )
        }
    }
}