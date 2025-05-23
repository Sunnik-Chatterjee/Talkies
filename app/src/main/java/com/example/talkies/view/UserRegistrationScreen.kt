package com.example.talkies.view

import com.example.talkies.vm.LoginViewModel
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.talkies.R
import com.example.talkies.navigation.Screens
import com.example.talkies.state.UiState
import com.example.talkies.view.Util.CountryData

@Composable
fun UserRegistrationScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val haptic = LocalHapticFeedback.current

    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember {
        mutableStateOf(CountryData.countries.first { it.name == "India" })
    }
    var wrongNumberInput by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }

    // Handle navigation when code is sent
    LaunchedEffect(authState) {
        when (authState) {
            is UiState.CodeSent -> {
                navController.navigate("${Screens.OtpScreen.routes}/${phoneNumber}/${selectedCountry.code}") {
                    popUpTo(Screens.UserRegistrationScreen.routes) {
                        inclusive = false
                    }
                }
                isSendingOtp = false
            }

            is UiState.Failed -> {
                isSendingOtp = false
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Enter your phone number",
            fontSize = 20.sp,
            color = colorResource(id = R.color.purple_500),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Talkies will send an SMS message to verify your phone number.",
            textAlign = TextAlign.Center,
            color = colorResource(R.color.purple_200)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Country Selection Dropdown
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.width(230.dp)) {
                Text(
                    text = selectedCountry.name,
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select country",
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = colorResource(R.color.purple_200)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 66.dp),
            thickness = 2.dp,
            color = colorResource(R.color.purple_200)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CountryData.countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(text = country.name) },
                    onClick = {
                        selectedCountry = country
                        expanded = false
                    }
                )
            }
        }

        // Phone Number Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = selectedCountry.code,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.width(70.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = colorResource(R.color.purple_200),
                    focusedIndicatorColor = colorResource(R.color.purple_200)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = phoneNumber,
                onValueChange = { newNumber ->
                    if (newNumber.length <= selectedCountry.phoneLength && newNumber.all { it.isDigit() }) {
                        phoneNumber = newNumber
                        wrongNumberInput = false
                    }
                },
                placeholder = { Text("Phone number") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = colorResource(R.color.purple_200),
                    focusedIndicatorColor = colorResource(R.color.purple_200)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Carrier charges may apply",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Error Messages
        if (wrongNumberInput) {
            Text(
                text = "Please enter a valid ${selectedCountry.name} phone number (${selectedCountry.phoneLength} digits)",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        when (val state = authState) {
            is UiState.Failed -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Send OTP Button
        Button(
            onClick = {
                if (phoneNumber.length != selectedCountry.phoneLength) {
                    wrongNumberInput = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    return@Button
                }

                wrongNumberInput = false
                isSendingOtp = true
                val fullPhoneNumber = "${selectedCountry.code}${phoneNumber}"
                viewModel.sendVerificationCode(fullPhoneNumber, activity)
            },
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            enabled = !isSendingOtp
        ) {
            if (isSendingOtp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending OTP...", fontSize = 16.sp)
                }
            } else {
                Text("Send OTP", fontSize = 16.sp)
            }
        }
    }
}