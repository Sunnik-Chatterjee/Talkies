package com.example.talkies.view.LoginScreen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.talkies.view.Util.CountryData
import com.example.talkies.vm.LoginViewModel

@Composable

fun UserRegistrationScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    val haptic = LocalHapticFeedback.current


    var expanded by remember {
        mutableStateOf(false)
    }
    var selectedCountry by remember {
        mutableStateOf(CountryData.countries.first { it.name == "India" })
    }
    var wrongNumberInput by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
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
            "Talkies will send an sms message to verify your phone number. " +
                    "Enter your country and phone number",
            textAlign = TextAlign.Center,
            color = colorResource(R.color.purple_200)
        )
        Spacer(modifier = Modifier.width(4.dp))
        TextButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(230.dp)) {
                Text(
                    selectedCountry.name, modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp, color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.align(
                        Alignment.CenterEnd,
                    ),
                    tint = colorResource(R.color.purple_200)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 66.dp),
            thickness = 2.dp,
            color = colorResource(R.color.purple_200)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CountryData.countries.forEach { country ->
                DropdownMenuItem(text = { Text(country.name) }, onClick = {
                    selectedCountry = country
                    expanded = false
                })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
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
                Spacer(modifier = Modifier.width(7.dp))
                TextField(
                    value = phoneNumber,
                    onValueChange = { newnumber ->
                        if (newnumber.length <= selectedCountry.phoneLength && newnumber.all { it.isDigit() })
                            phoneNumber = newnumber
                    },
                    placeholder = { Text("Phone Number") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = colorResource(R.color.purple_200),
                        focusedIndicatorColor = colorResource(R.color.purple_200)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Carrier charges may apply",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (wrongNumberInput) {
                Text(
                    "Please give the correct number",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                if (phoneNumber.length != selectedCountry.phoneLength) {
                    wrongNumberInput = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                } else {
                    val fullphoneNumber = "${selectedCountry.code}${phoneNumber}"
                    viewModel.sendVerificationCode(fullphoneNumber, activity)
                    if(authState is UiState.CodeSent) {
                        navController.navigate("${Screens.OtpScreen.routes}/${phoneNumber}/${selectedCountry.code}")
                    }
                }
            }, shape = RoundedCornerShape(6.dp)) {
                Text("Send OTP", fontSize = 16.sp)
            }
        }


    }
}