package com.example.kongsikeretariders.ui.rides

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kongsikeretariders.data.Rides
import com.example.kongsikeretariders.util.JsonHelper
import com.example.kongsikeretariders.util.PreferencesManager
import com.example.kongsikeretariders.util.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetailedRideCardViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _detailedRideCardUiState = MutableStateFlow(DetailedRideCardUiState())
    val detailedRideCardUiState = _detailedRideCardUiState.asStateFlow()

    fun initRide(ride: Rides, context: Context) {
        var isJoined: Boolean? = null
        var isCancelled: Boolean? = null
        val userId = PreferencesManager.getStringPreference("userId")
        var driverInfo: HashMap<String, File?> = hashMapOf()
        viewModelScope.launch {
            val onGoingRide = userRepository.getOnGoingRide(rides = ride)

            Log.i("onGoingRideInCardView", onGoingRide.toString())

            isJoined = onGoingRide.joined.contains(userId)
            isCancelled = onGoingRide.cancelled.contains(userId)

            Log.i("ride user Id", ride.userId)
            driverInfo = userRepository.fetchDriverInfo(ride.userId, context)

            val profilePic = driverInfo["profilePic"]
            Log.i("driverinfo", driverInfo["userData"].toString())
            val driver = JsonHelper.parseJson<UserDriver>(driverInfo["userData"]!!)

            _detailedRideCardUiState.update {
                DetailedRideCardUiState(
                    ride = ride,
                    isJoined = isJoined!!,
                    isCancelled = isCancelled!!,
                    driverInfo = driver!!,
                    driverProfilePic = profilePic
                )
            }

        }


    }

    private fun updateIsJoined(bool: Boolean) {
        _detailedRideCardUiState.update { currentState ->
            currentState.copy(isJoined = bool)
        }
    }

    private fun updateIsCancelled(bool: Boolean) {
        _detailedRideCardUiState.update { currentState ->
            currentState.copy(isCancelled = bool)
        }
    }

    fun cancelRide() {
        updateIsCancelled(true)
        updateIsJoined(false)
        viewModelScope.launch {
            userRepository.updateRide(
                _detailedRideCardUiState.value.ride,
                callback = { },
                isJoin = false
            )
        }

    }


    fun joinRide() {
        updateIsJoined(true)
        updateIsCancelled(false)

        viewModelScope.launch {
            userRepository.updateRide(
                _detailedRideCardUiState.value.ride,
                callback = {},
                isJoin = true
            )
        }
    }
}

data class DetailedRideCardUiState(
    val ride: Rides = Rides(),
    val isJoined: Boolean = false,
    val isCancelled: Boolean = false,
    val driverInfo: UserDriver = UserDriver(),
    val driverProfilePic: File? = null
)

data class UserDriver(
    val ic: String = "",
    val password: String = "",
    val gender: Boolean = false,
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val vehicle: Vehicle = Vehicle()
)

data class Vehicle(
    val model: String = "",
    val capacity: Int = 0,
    val notes: String = "",
)

data class UserDriverInfo(
    val ic: String = "",
    val password: String = "",
    val profilePicUrl: String = "",
    val userDataUrl: String = "",
)