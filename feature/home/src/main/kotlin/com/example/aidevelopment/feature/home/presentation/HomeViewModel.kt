package com.example.aidevelopment.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.example.aidevelopment.feature.home.presentation.mvi.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Init -> {
                // TODO: Implement initialization logic
            }
        }
    }
}
