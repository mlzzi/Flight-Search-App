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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mluzzi.flightseachapp.data.Airport

@Composable
fun FlightItem(
    departAirport: Airport,
    arriveAirport: Airport,
    modifier: Modifier = Modifier,
    viewModel: FlightsViewModel
) {
    val isFavorite = viewModel.favoriteFlights.collectAsState(initial = emptyList())
        .value.any { it.departureCode == departAirport.iataCode && it.destinationCode == arriveAirport.iataCode }
//    var id by remember { mutableStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("DEPART")
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
                Text("ARRIVE")
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
                contentDescription = "Favorite",
                modifier = Modifier.clickable {
                    viewModel.insertOrDeleteFavorite(departAirport, arriveAirport)
                },
                tint = if (isFavorite) Color.Yellow else Color.Gray
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FlightItemPreview() {
//    FlightItem(
////        airport = Airport(
////            id = 1,
////            name = "São Paulo Guarulhos International Airport",
////            iataCode = "GRU",
////            passengers = 1000
////        )
//        flightItem = FlightItem(
//            departAirport = "GRU",
//            arriveAirport = "GIG"
//        )
//    )
//}