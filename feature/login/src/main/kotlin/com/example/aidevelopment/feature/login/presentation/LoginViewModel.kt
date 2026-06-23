package com.example.aidevelopment.feature.login.presentation

import androidx.lifecycle.ViewModel
import com.example.aidevelopment.feature.login.presentation.mvi.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Init -> {
                // TODO: Implement initialization logic
            }
        }
    }
}
