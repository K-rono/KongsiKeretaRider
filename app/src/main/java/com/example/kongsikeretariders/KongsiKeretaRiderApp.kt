package com.example.kongsikeretariders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kongsikeretariders.ui.login.LoginScreen
import com.example.kongsikeretariders.ui.login.LoginScreenViewModel
import com.example.kongsikeretariders.ui.login.RegisterScreen
import com.example.kongsikeretariders.ui.login.RegisterScreenViewModel
import com.example.kongsikeretariders.ui.rider.RiderProfileScreen
import com.example.kongsikeretariders.ui.rider.RiderProfileScreenViewModel
import com.example.kongsikeretariders.ui.rides.DetailedRideCard
import com.example.kongsikeretariders.ui.rides.DetailedRideCardViewModel
import com.example.kongsikeretariders.ui.rides.MyRidesScreen
import com.example.kongsikeretariders.ui.rides.ViewRidesAllScreen
import com.example.kongsikeretariders.ui.rides.ViewRidesViewModel
import com.example.kongsikeretariders.util.PreferencesManager

enum class KongsiKeretaRiderScreens() {
    LOGIN(),
    REGISTRATION(),
    RIDER_PROFILE(),
    RIDE_VIEWS_ALL(),
    RIDE_VIEWS_JOINED(),
    RIDE_VIEWS_DETAILED(),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KongsiKeretaRiderApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier
) {
    val context = LocalContext.current
    PreferencesManager.init(context)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route
        ?: if (PreferencesManager.getBooleanPreference("isLoggedIn")) KongsiKeretaRiderScreens.RIDER_PROFILE.name else KongsiKeretaRiderScreens.LOGIN.name

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "KongsiKereta") }) }, bottomBar = {
            if (currentScreen !in listOf(
                    KongsiKeretaRiderScreens.LOGIN.name, KongsiKeretaRiderScreens.REGISTRATION.name, KongsiKeretaRiderScreens.RIDE_VIEWS_DETAILED.name
                )
            ) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { navController.navigate(route = KongsiKeretaRiderScreens.RIDER_PROFILE.name) }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Page"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Profile")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { navController.navigate(route = KongsiKeretaRiderScreens.RIDE_VIEWS_ALL.name) }) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Find Rides"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Find Rides")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { navController.navigate(route = KongsiKeretaRiderScreens.RIDE_VIEWS_JOINED.name) }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "My Rides"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "My Rides")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val loginScreenViewModel: LoginScreenViewModel = hiltViewModel()
        val registerScreenViewModel: RegisterScreenViewModel = hiltViewModel()
        val riderProfileScreenViewModel: RiderProfileScreenViewModel = hiltViewModel()
        val viewRidesAllViewModel: ViewRidesViewModel = hiltViewModel()
        val detailedRideCardViewModel: DetailedRideCardViewModel = hiltViewModel()
        val viewMyRidesViewModel: ViewRidesViewModel = hiltViewModel()
        NavHost(
            navController = navController,
            startDestination = if (PreferencesManager.getBooleanPreference("isLoggedIn")) KongsiKeretaRiderScreens.RIDER_PROFILE.name else KongsiKeretaRiderScreens.LOGIN.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = KongsiKeretaRiderScreens.LOGIN.name) {
                LoginScreen(
                    loginScreenViewModel = loginScreenViewModel,
                    registerClicked = { navController.navigate(KongsiKeretaRiderScreens.REGISTRATION.name) }) {
                    navController.navigate(KongsiKeretaRiderScreens.RIDER_PROFILE.name)
                }
            }

            composable(route = KongsiKeretaRiderScreens.REGISTRATION.name) {
                RegisterScreen(registerScreenViewModel = registerScreenViewModel) {
                    navController.navigateUp()
                }
            }

            composable(route = KongsiKeretaRiderScreens.RIDER_PROFILE.name) {
                RiderProfileScreen(riderProfileScreenViewModel = riderProfileScreenViewModel) {
                    navController.navigate(KongsiKeretaRiderScreens.LOGIN.name)
                }
            }

            composable(route = KongsiKeretaRiderScreens.RIDE_VIEWS_ALL.name) {
                ViewRidesAllScreen(viewRideViewModel = viewRidesAllViewModel, detailedRideCardViewModel = detailedRideCardViewModel){
                    navController.navigate(route = KongsiKeretaRiderScreens.RIDE_VIEWS_DETAILED.name)
                }
            }

            composable(route = KongsiKeretaRiderScreens.RIDE_VIEWS_DETAILED.name ){
                DetailedRideCard(detailedRideCardViewModel = detailedRideCardViewModel){
                    navController.navigateUp()
                }
            }

            composable(route = KongsiKeretaRiderScreens.RIDE_VIEWS_JOINED.name){
                MyRidesScreen(viewRideViewModel = viewMyRidesViewModel)
            }
        }
    }
}

