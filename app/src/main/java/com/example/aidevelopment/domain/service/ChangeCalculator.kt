package com.example.aidevelopment.domain.service

import com.example.aidevelopment.domain.model.Coin
import com.example.aidevelopment.domain.model.Currency

/**
 * Domain Service to calculate optimal change using a Greedy Algorithm.
 * Ensuring we use the minimum number of coins from the available vault supply.
 */
class ChangeCalculator {
    fun calculateChange(
        amountNeeded: Currency,
        availableVault: Map<Coin, Int>,
    ): List<Coin>? {
        if (amountNeeded.amount == 0) return emptyList()

        val result = mutableListOf<Coin>()
        var remaining = amountNeeded.amount
        val tempVault = availableVault.toMutableMap()

        // Use largest coins first (Greedy approach)
        for (coin in Coin.availableDenominationsDesc) {
            while (remaining >= coin.value.amount && (tempVault[coin] ?: 0) > 0) {
                remaining -= coin.value.amount
                result.add(coin)
                tempVault[coin] = tempVault[coin]!! - 1
            }
        }

        return if (remaining == 0) result else null
    }
}
