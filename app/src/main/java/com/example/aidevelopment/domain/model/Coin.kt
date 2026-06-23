package com.example.aidevelopment.domain.model

enum class Coin(val value: Currency) {
    ONE(Currency(1)),
    FIVE(Currency(5)),
    TEN(Currency(10));

    companion object {
        val availableDenominationsDesc = Coin.entries.toTypedArray().sortedByDescending { it.value.amount }

        fun fromInt(amount: Int): Coin = Coin.entries.find { it.value.amount == amount }
            ?: throw IllegalArgumentException("Invalid coin denomination: $amount")
    }
}