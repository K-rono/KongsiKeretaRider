package com.example.kongsikeretariders.ui.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kongsikeretariders.util.genders

@Composable
fun RegisterScreen(
    registerScreenViewModel: RegisterScreenViewModel,
    loggedIn: () -> Unit,
) {
    val config = LocalConfiguration.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val uiState = registerScreenViewModel.registerScreenUiState.collectAsState()
    val imageUri by registerScreenViewModel.imageUri.collectAsState()
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            registerScreenViewModel.updateImageUri(uri)
        })
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        ElevatedCard(
            modifier = Modifier.width((config.screenWidthDp * 0.8).dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    Text(text = "Add Profile Pic")
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { imageLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Profile Picture"
                        )
                    }
                }
                if (imageUri != null) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.value.ic,
                    onValueChange = { registerScreenViewModel.updateIc(it) },
                    label = { Text(text = "Identification Number") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.value.password,
                    onValueChange = { registerScreenViewModel.updatePassword(it) },
                    label = { Text(text = "Password") },
                    singleLine = true,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ElevatedCard(
            modifier = Modifier.width((config.screenWidthDp * 0.8).dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    TextField(
                        value = if (uiState.value.gender) "Male" else "Female",
                        onValueChange = {},
                        readOnly = true
                    )
                    IconButton(onClick = { registerScreenViewModel.updateGenderPickerState(true) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Gender"
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.value.isGenderPickerExpanded,
                        onDismissRequest = { registerScreenViewModel.updateGenderPickerState(false) }) {
                        genders.forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(text = gender) },
                                onClick = {
                                    registerScreenViewModel.updateGender(
                                        gender == "Male"
                                    )
                                    registerScreenViewModel.updateGenderPickerState(false)
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.value.email,
                    onValueChange = { registerScreenViewModel.updateEmail(it) },
                    label = { Text(text = "Email") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.value.phoneNumber,
                    onValueChange = { registerScreenViewModel.updatePhoneNum(it) },
                    label = { Text(text = "Phone Number") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = uiState.value.address,
                    onValueChange = { registerScreenViewModel.updateAddress(it) },
                    label = { Text(text = "Address") },
                )
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = {
                registerScreenViewModel.registerUser(context) { success ->
                    if (success) {
                        loggedIn()
                    }
                }
            },
        ) {
            Text(text = "Register")
        }
    }
}
