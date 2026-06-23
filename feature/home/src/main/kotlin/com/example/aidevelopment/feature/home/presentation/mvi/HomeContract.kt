package com.example.aidevelopment.feature.home.presentation.mvi

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HomeIntent {
    object Init : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val message: String) : HomeEffect
}
