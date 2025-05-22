package com.example.talkies.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.talkies.R
import com.example.talkies.navigation.Screens
import com.example.talkies.ui.theme.DeepPurple700
import com.example.talkies.vm.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    navHostController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    var startAnimation by remember { mutableStateOf(false) }
    val isLoggedIn = viewModel.isLoggedIn.collectAsState()
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(4000)
        navHostController.popBackStack()
        if (isLoggedIn.value) {
            navHostController.navigate(Screens.HomeScreen.routes)
        } else navHostController.navigate(
            Screens.UserRegistrationScreen.routes
        )
    }
    Splash(alpha = alphaAnim.value)
}
@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple700)
            .padding(40.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.talkies), contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(60.dp))
                .alpha(alpha = alpha)
        )
    }
}
