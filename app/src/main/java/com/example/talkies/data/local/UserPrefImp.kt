package com.example.talkies.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


class UserPrefImp(private val dataStore:DataStore<Preferences>): UserPref {
    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit {
            it[USER_KEY]= isLoggedIn
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.catch { emit(emptyPreferences()) }.map {
            it[USER_KEY]?:false
        }
    }

}