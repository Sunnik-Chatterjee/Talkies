package com.example.talkies.view.LoginScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.talkies.R
import com.example.talkies.navigation.Screens
import com.example.talkies.state.UiState
import com.example.talkies.vm.LoginViewModel

@Composable
fun OtpScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel(),
    phoneNumber: String,
    countryCode: String
) {

    val otp = viewModel.autoRetrievedOtp.collectAsState()
    val context =  LocalContext.current
    val authState = viewModel.authState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Verify ${countryCode}-${phoneNumber}", fontSize = 20.sp,
            color = colorResource(id = R.color.purple_500),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        .clickable { navController.navigate(Screens.UserRegistrationScreen.routes) }
                )
            }
        }
        TextField(
            value = otp.value,
            onValueChange = { viewModel.onOtpEntered(it) }, // Optional if user edits
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
        Button(onClick = {
            viewModel.verifyCode(otp.value, context = context)
        }) {
            Text("Verify OTP")
        }
        when(authState){
            is UiState.Loading->{
                CircularProgressIndicator()
            }
            is UiState.Failed ->{
                val message = authState.message
                Log.e("OTP_ERROR", message)
                Toast.makeText(context,"Please Enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
            is UiState.Success<*> -> {
                Text("Correct OTP welcome to talkies")
            }
            else ->{}
        }

    }
}