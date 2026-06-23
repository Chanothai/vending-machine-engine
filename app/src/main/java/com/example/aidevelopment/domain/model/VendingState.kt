package com.example.aidevelopment.domain.model

sealed interface VendingState {
    object Idle : VendingState

    data class HasBalance(
        val balance: Currency,
        val insertedCoins: List<Coin>,
    ) : VendingState
}
