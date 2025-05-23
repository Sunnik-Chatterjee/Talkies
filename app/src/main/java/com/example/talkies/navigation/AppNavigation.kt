package com.example.talkies.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.talkies.view.AnimatedSplashScreen
import com.example.talkies.view.HomeScreen
import com.example.talkies.view.OtpScreen
import com.example.talkies.view.UserRegistrationScreen
import com.example.talkies.vm.LoginViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val viewModel: LoginViewModel = hiltViewModel()
    val startDestination = Screens.SplashScreen.routes
    NavHost(navController, startDestination) {
        composable(Screens.SplashScreen.routes) {
            AnimatedSplashScreen(navController)
        }
        composable(Screens.UserRegistrationScreen.routes) {
            UserRegistrationScreen(navController = navController, viewModel = viewModel)
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
                OtpScreen(
                    navController,
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    viewModel = viewModel
                )
            }
        }
        composable(Screens.HomeScreen.routes) {
            HomeScreen(navController = navController)
        }

    }
}