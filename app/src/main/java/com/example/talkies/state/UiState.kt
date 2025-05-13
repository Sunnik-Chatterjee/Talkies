package com.example.talkies.state


sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val user: T) : UiState<T>
    data class CodeSent(val verificationId: String): UiState<Nothing>
    data class Failed(val message: String) : UiState<Nothing>
}