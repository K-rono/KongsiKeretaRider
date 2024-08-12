package com.example.kongsikeretariders.ui.rider

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kongsikeretariders.util.PreferencesManager

@Composable
fun RiderProfileScreen(
    riderProfileScreenViewModel: RiderProfileScreenViewModel,
    onLogOutClicked: () -> Unit
) {
    val context = LocalContext.current

    if(!riderProfileScreenViewModel.isDataFetched || PreferencesManager.getBooleanPreference("isPreviousUser")){
        Log.i("isLaunchedEffectINvoked","yes")
        LaunchedEffect(riderProfileScreenViewModel.isDataFetched) {
            riderProfileScreenViewModel.loadDriverProfile(context)
            riderProfileScreenViewModel.isDataFetched = true
        }
    }
    val scrollState = rememberScrollState()
    val profileUiState = riderProfileScreenViewModel.riderProfileUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        ElevatedCard {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (profileUiState.value.profilePic != null) {
                    val bitMap =
                        BitmapFactory.decodeFile(profileUiState.value.profilePic!!.absolutePath)
                    if (bitMap != null) {
                        Box(modifier = Modifier.width(200.dp).height(200.dp)){
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "IC  :  ${profileUiState.value.driver.ic}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Gender  :  ${if (profileUiState.value.driver.gender) "Male" else "Female"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Phone No.  :  ${profileUiState.value.driver.phoneNumber}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "E-mail  :  ${profileUiState.value.driver.email}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Address  :  ${profileUiState.value.driver.address}")
            }

        }
        Spacer(modifier = Modifier.height(64.dp))
        Button(onClick = { riderProfileScreenViewModel.logOut()
            PreferencesManager.putPreference("isPreviousUser",true)
            onLogOutClicked()}) {
            Text(text = "Log Out")
        }
    }
}