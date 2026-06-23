package com.example.aidevelopment.feature.login.presentation.mvi

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginIntent {
    object Init : LoginIntent
}

sealed interface LoginEffect {
    data class ShowError(val message: String) : LoginEffect
}
