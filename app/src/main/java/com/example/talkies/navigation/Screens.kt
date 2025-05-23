package com.example.talkies.navigation

sealed class Screens(val routes: String) {
     object SplashScreen: Screens("splash_Screen")
     object UserRegistrationScreen: Screens("userRegistration_Screen")
     object OtpScreen: Screens("otp_Screen")
     object UserProfileSetScreen: Screens("userProfileSet_Screen")
     object HomeScreen: Screens("home_Screen")
     object ChatScreen: Screens("chat_Screen")
}