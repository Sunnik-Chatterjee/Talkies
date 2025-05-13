package com.example.talkies.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val isUserLoggedIn = Firebase.auth.currentUser!=null
    val startDestination = if(isUserLoggedIn) "" else "login_graph"

    NavHost(navController = navController,startDestination = startDestination){

    }
}