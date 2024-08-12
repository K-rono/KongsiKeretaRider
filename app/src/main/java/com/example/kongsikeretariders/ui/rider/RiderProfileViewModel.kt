package com.example.kongsikeretariders.ui.rider

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kongsikeretariders.data.Rider
import com.example.kongsikeretariders.util.JsonHelper
import com.example.kongsikeretariders.util.PreferencesManager
import com.example.kongsikeretariders.util.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RiderProfileScreenViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel(){
    private val _riderProfileUiState = MutableStateFlow(driverProfileUiState())
    val riderProfileUiState: StateFlow<driverProfileUiState> = _riderProfileUiState.asStateFlow()

    var isDataFetched = userRepository.currentRider != null
    fun loadDriverProfile(context: Context){
        viewModelScope.launch {
            var value = userRepository.currentRider
            if(isDataFetched == false){
                val userId = PreferencesManager.getStringPreference("userId")
                val password = PreferencesManager.getStringPreference("password")
                userRepository.signIn(userId,password,context){}
                value = userRepository.currentRider
                Log.i("currentUser",value.toString())

            }
            if(value != null){
                val profilePic = value["profilePic"]
                val driver = JsonHelper.parseJson<Rider>(value["userData"]!!)

                _riderProfileUiState.update { currentState ->
                    currentState.copy(
                        driver = driver!!,
                        profilePic = profilePic
                    )
                }
            }
        }
    }

    fun logOut(){
        PreferencesManager.clearPreferences()
        userRepository.currentRider = null
    }
}

data class driverProfileUiState(
    val driver: Rider = Rider(),
    val profilePic: File? = null,
)