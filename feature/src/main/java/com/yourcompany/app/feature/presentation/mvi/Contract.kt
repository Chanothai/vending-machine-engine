package com.yourcompany.app.feature..presentation.mvi

data class State(
    val isLoading: Boolean = false
)

sealed interface Intent {
    object Init : Intent
}

sealed interface Effect {
    data class ShowError(val message: String) : Effect
}
