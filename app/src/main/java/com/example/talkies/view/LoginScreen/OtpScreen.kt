package com.example.talkies.view.LoginScreen

import android.widget.Toast
import com.example.talkies.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.talkies.navigation.Screens
import com.example.talkies.state.UiState
import com.example.talkies.vm.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: LoginViewModel,
    phoneNumber: String,
    countryCode: String
) {
    val otp = viewModel.autoRetrievedOtp.collectAsState()
    val context = LocalContext.current
    val authState = viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Verify ${countryCode}-${phoneNumber}",
            fontSize = 20.sp,
            color = colorResource(R.color.purple_700),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Waiting to automatically detect an SMS sent to $countryCode-$phoneNumber.",
                    color = colorResource(R.color.purple_200),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Wrong number?",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            viewModel.resetAuthState()
                            navController.navigate(Screens.UserRegistrationScreen.routes)
                        }
                )
            }
        }

        TextField(
            value = otp.value,
            onValueChange = viewModel::onOtpEntered,
            label = { Text("Enter OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (otp.value.length == 6) { // Assuming 6-digit OTP
                    viewModel.verifyCode(otp.value, context)
                } else {
                    Toast.makeText(context, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState.value !is UiState.Loading
        ) {
            if (authState.value is UiState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifying...")
            } else {
                Text("Verify OTP")
            }
        }

        when (val state = authState.value) {
            is UiState.Failed -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LaunchedEffect(state) {
                    delay(3000)
                    viewModel.resetAuthState()
                }
            }
            is UiState.Success<*> -> {
                LaunchedEffect(Unit) {
                    navController.navigate(Screens.HomeScreen.routes) {
                        popUpTo(Screens.UserRegistrationScreen.routes) {
                            inclusive = true
                        }
                    }
                }
            }
            else -> {}
        }
    }
}