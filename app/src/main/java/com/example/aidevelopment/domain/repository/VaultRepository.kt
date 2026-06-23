package com.example.aidevelopment.domain.repository

import com.example.aidevelopment.domain.model.Coin

interface VaultRepository {
    fun getCoinCount(coin: Coin): Int
    fun addCoins(coins: List<Coin>)
    fun removeCoins(coins: List<Coin>)
    fun getSnapshot(): Map<Coin, Int>
}