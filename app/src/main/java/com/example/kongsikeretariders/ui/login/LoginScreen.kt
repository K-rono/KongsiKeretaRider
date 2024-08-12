package com.example.kongsikeretariders.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kongsikeretariders.util.PreferencesManager

@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel,
    registerClicked: () -> Unit,
    loginMatched: () -> Unit,
){
    val context = LocalContext.current
    val uiState = loginScreenViewModel.loginUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Login", fontWeight = FontWeight.Bold, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = uiState.value.userId, onValueChange = {loginScreenViewModel.updateId(it)}, label = { Text(
            text = "Identification Number"
        )
        })
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = uiState.value.password, onValueChange = {loginScreenViewModel.updatePassword(it)}, label = { Text(
            text = "Password"
        )
        }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        if(uiState.value.isIncorrect){
            Text(text = "Incorrect Credentials")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { loginScreenViewModel.logIn(context){success ->
                if(success){
                    PreferencesManager.putPreference("isLoggedIn",true)
                    loginMatched()
                }
            } }) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { registerClicked() }) {
                Text(text = "Not A User?")
            }
        }
    }
}