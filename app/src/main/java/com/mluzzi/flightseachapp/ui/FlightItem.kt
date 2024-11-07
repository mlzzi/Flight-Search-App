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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mluzzi.flightseachapp.R
import com.mluzzi.flightseachapp.data.model.Airport

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

                ItemRow(departAirport)

                Spacer(modifier = Modifier.height(8.dp))

                ItemRow(arriveAirport)
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

@Composable
fun ItemRow(
    arriveAirport: Airport,
    modifier: Modifier = Modifier
) {
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