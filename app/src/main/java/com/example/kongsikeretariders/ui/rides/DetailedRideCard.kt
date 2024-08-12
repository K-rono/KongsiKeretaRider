package com.example.kongsikeretariders.ui.rides

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.kongsikeretariders.util.DateConverter

@Composable
fun DetailedRideCard(
    detailedRideCardViewModel: DetailedRideCardViewModel,
    onBackPressed: () -> Unit
) {
    val config = LocalConfiguration.current
    val uiState = detailedRideCardViewModel.detailedRideCardUiState.collectAsState()
    val scrollState = rememberScrollState()
    Column {
        Row(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                onBackPressed()
                Log.i("backPressed", "yes")
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back button")
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.7).dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Date  :    ${DateConverter.millisToFormattedDate(uiState.value.ride.date)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Time  :    ${uiState.value.ride.time}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Origin  :    ${uiState.value.ride.origin}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Destination  :    ${uiState.value.ride.destination}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Fare(RM)  :    ${uiState.value.ride.fare}")
                }

            }
            ElevatedCard(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.7).dp)
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (uiState.value.driverProfilePic != null) {
                        val bitMap =
                            BitmapFactory.decodeFile(uiState.value.driverProfilePic!!.absolutePath)
                        if (bitMap != null) {
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                            ) {
                                Image(
                                    bitmap = bitMap.asImageBitmap(),
                                    contentDescription = "Profile Picture"
                                )
                            }

                        } else {
                            Text(text = "Failed to load profile picture")
                        }
                    } else {
                        Text(text = "No profile picture available")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Driver Phone Number  :    ${uiState.value.driverInfo.phoneNumber}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Driver E-mail  :    ${uiState.value.driverInfo.email}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Driver Address  :    ${uiState.value.driverInfo.address}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Car Model  :    ${uiState.value.driverInfo.vehicle.model}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Capacity :    ${uiState.value.driverInfo.vehicle.capacity}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Extra Feature :    ${uiState.value.driverInfo.vehicle.notes}")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.value.isJoined) {
                Button(onClick = { detailedRideCardViewModel.cancelRide() }) {
                    Text(text = "Cancel Ride")
                }
            } else {
                Button(onClick = { detailedRideCardViewModel.joinRide() }) {
                    Text(text = "Join Ride")
                }
            }

        }
    }


}