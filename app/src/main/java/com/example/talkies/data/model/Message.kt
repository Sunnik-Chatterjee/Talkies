package com.example.talkies.data.model

data class Message(
    val sendersPhoneNumber: String="",
    val message: String="",
    val timeStamp : Long = 0L
)