package com.example.talkies.state

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data object CodeSent : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Failed(val message: String) : UiState<Nothing>
}