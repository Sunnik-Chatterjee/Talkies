package com.example.talkies.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.talkies.view.LoginScreen.OtpScreen
import com.example.talkies.view.LoginScreen.UserRegistrationScreen
import com.example.talkies.view.SplashScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val startDestination = Screens.UserRegistrationScreen.routes
    NavHost(navController, startDestination) {
        composable(Screens.SplashScreen.routes) {
            SplashScreen(navController)
        }
        composable (Screens.UserRegistrationScreen.routes){
            UserRegistrationScreen(navController = navController)
        }
        val detailScreen = Screens.OtpScreen.routes
        composable(
            route = "$detailScreen/{phoneNumber}/{countryCode}",
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("countryCode") { type = NavType.StringType }
            )) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")
            val countryCode = backStackEntry.arguments?.getString("countryCode")

            if (phoneNumber != null && countryCode != null) {
                OtpScreen(navController, countryCode = countryCode, phoneNumber = phoneNumber)
            }
        }

    }
}