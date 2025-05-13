package com.example.talkies.navigation

sealed class Screens(val routes: String){
    object chatList: Screens("chatList")
    object chatScreen: Screens("chatScreen")
}