package com.example.kongsikeretariders.ui.rides

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kongsikeretariders.data.Rides
import com.example.kongsikeretariders.util.DateConverter

@Composable
fun ViewRidesAllScreen(
    viewRideViewModel: ViewRidesViewModel,
    detailedRideCardViewModel: DetailedRideCardViewModel,
    onJoinClicked: () -> Unit,
) {
    val isDataFetched = viewRideViewModel.isDataFetched.collectAsState()
    if (!isDataFetched.value) {
        LaunchedEffect(!isDataFetched.value) {
            Log.i("loadRidesInvoked", "Yes")
            viewRideViewModel.loadRides()
        }
    }
    val uiState = viewRideViewModel.viewRidesAllUiState.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(uiState.value.ridesList) { ride ->
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumnItem(
                ride = ride,
                detailedRideCardViewModel = detailedRideCardViewModel,
                onJoinClicked = onJoinClicked
            )
        }
    }
}

@Composable
fun LazyColumnItem(
    ride: Rides,
    detailedRideCardViewModel: DetailedRideCardViewModel,
    onJoinClicked: () -> Unit
) {
    val config = LocalConfiguration.current
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier.width((config.screenWidthDp * 0.7).dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Date  :    ${DateConverter.millisToFormattedDate(ride.date)}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Time  :    ${ride.time}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Origin  :    ${ride.origin}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Destination  :    ${ride.destination}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fare(RM)  :    ${ride.fare}")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                detailedRideCardViewModel.initRide(ride, context)
                onJoinClicked()
            }) {
                Text(text = "Join Ride")
            }
        }

    }
}