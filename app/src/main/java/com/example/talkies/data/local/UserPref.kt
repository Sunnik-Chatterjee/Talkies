package com.example.talkies.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
val USER_KEY = booleanPreferencesKey("user_loggedIn")

interface UserPref {
    suspend fun setLoggedIn(isLoggedIn: Boolean)
    fun isLoggedIn(): Flow<Boolean>
}