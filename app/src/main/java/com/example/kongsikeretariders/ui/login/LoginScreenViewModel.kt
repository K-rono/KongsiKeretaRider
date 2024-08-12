package com.example.kongsikeretariders.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kongsikeretariders.util.PreferencesManager
import com.example.kongsikeretariders.util.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun updateId(id: String) {
        _loginUiState.update { currentState ->
            currentState.copy(userId = id)
        }
    }

    fun updatePassword(password: String) {
        _loginUiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    fun updateAuthState(state: Boolean) {
        _loginUiState.update { currentState ->
            currentState.copy(isIncorrect = state)
        }
    }

    fun logIn(
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        var isSuccess = false
        val value = _loginUiState.value
        if (value.userId == "" || value.password == "") {
            Toast.makeText(context, "Error, Fields Empty", Toast.LENGTH_LONG).show()
            callback(false)
            return
        }
        viewModelScope.launch {
            userRepository.signIn(
                userId = value.userId,
                password = value.password,
                context = context
            ) { success ->
                isSuccess = success
                _loginUiState.update { currentState -> currentState.copy(isIncorrect = !success) }
            }
        }.invokeOnCompletion {
            if (isSuccess) {
                PreferencesManager.putPreference("userId", value.userId)
                PreferencesManager.putPreference("password", value.password)
                _loginUiState.update {
                    LoginUiState()
                }
            } else {
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_LONG).show()

            }

            callback(isSuccess)
        }
    }

}

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val isIncorrect: Boolean = false
)