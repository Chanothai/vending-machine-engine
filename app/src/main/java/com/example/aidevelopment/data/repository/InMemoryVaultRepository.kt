package com.example.aidevelopment.data.repository

import com.example.aidevelopment.domain.model.Coin
import com.example.aidevelopment.domain.repository.VaultRepository

class InMemoryVaultRepository : VaultRepository {
    private val coins = Coin.entries.associateWith { 10 }.toMutableMap()

    override fun getCoinCount(coin: Coin): Int = coins[coin] ?: 0

    override fun addCoins(coins: List<Coin>) {
        coins.forEach { coin ->
            this@InMemoryVaultRepository.coins[coin] = (this@InMemoryVaultRepository.coins[coin] ?: 0) + 1
        }
    }

    override fun removeCoins(coins: List<Coin>) {
        coins.forEach { coin ->
            val current = this@InMemoryVaultRepository.coins[coin] ?: 0
            if (current > 0) this@InMemoryVaultRepository.coins[coin] = current - 1
        }
    }

    override fun getSnapshot(): Map<Coin, Int> = coins.toMap()
}
