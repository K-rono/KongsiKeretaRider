package com.example.kongsikeretariders.ui.rides

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kongsikeretariders.data.Rides
import com.example.kongsikeretariders.util.PreferencesManager
import com.example.kongsikeretariders.util.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewRidesViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _viewRidesAllUiState = MutableStateFlow(ViewRidesUiState())
    val viewRidesAllUiState: StateFlow<ViewRidesUiState> = _viewRidesAllUiState.asStateFlow()

    private val _viewRidesJoinedUiState = MutableStateFlow(ViewMyRidesUiState())
    val viewRidesJoinedUiState: StateFlow<ViewMyRidesUiState> =
        _viewRidesJoinedUiState.asStateFlow()

    private var _isDataFetched = MutableStateFlow(false)
    var isDataFetched = _isDataFetched.asStateFlow()

    private var _isMyRideDataFetched = MutableStateFlow(false)
    var isMyRideDataFetched = _isMyRideDataFetched.asStateFlow()

    fun loadRides() {
        val userId = PreferencesManager.getStringPreference("userId")
        viewModelScope.launch {
            val list = userRepository.fetchRides(null)
            Log.i("returnedList", list.toString())
            _viewRidesAllUiState.update {
                ViewRidesUiState(
                    list
                )
            }
            updateDataFetchedState()
        }
    }

    fun loadAllRides() {
        val userId = PreferencesManager.getStringPreference("userId")
        Log.i("userId",userId)
        var list: List<Rides> = listOf()
        viewModelScope.launch {
            list = userRepository.getAllOnGoingRides()
        }.invokeOnCompletion {
            Log.i("ride",list.toString())
            var joined: List<Rides> = listOf()

            val temp: List<Rides> = listOf()
            Log.i("rideno2",list[1].toString())
            Log.i("contains user id?" ,list[1].cancelled.contains(userId).toString())



            list.forEach {
                if (it.joined.contains(userId)) {
                    joined = joined.plus(it)
                }
            }

            var cancelled: List<Rides> = listOf()
            list.forEach {
                if (it.cancelled.contains(userId)) {
                    cancelled = cancelled.plus(it)
                }
            }

            var inactive: List<Rides> = listOf()
            list.forEach {
                val isBefore = Date(it.date).before(Date())
                val isJoined = (joined.contains(it))

                if (isJoined && isBefore) {
                    inactive = inactive.plus(it)
                }
            }

            _viewRidesJoinedUiState.update {
                ViewMyRidesUiState(joined, cancelled, inactive)
            }
            updateMyRideDataFetchedState()
            Log.i("uiStateViewMyRide",_viewRidesJoinedUiState.value.toString())
        }


    }

    private fun updateDataFetchedState() {
        _isDataFetched.update {
            _viewRidesAllUiState.value.ridesList.isNotEmpty()
        }
    }

    private fun updateMyRideDataFetchedState() {
        _isDataFetched.update {
            (_viewRidesJoinedUiState.value.joined.isNotEmpty() && _viewRidesJoinedUiState.value.cancelled.isNotEmpty() && _viewRidesJoinedUiState.value.inactive.isNotEmpty())
        }
    }
}

data class ViewRidesUiState(
    val ridesList: List<Rides> = listOf()
)

data class ViewMyRidesUiState(
    val joined: List<Rides> = emptyList(),
    val cancelled: List<Rides> = emptyList(),
    val inactive: List<Rides> = emptyList()
)