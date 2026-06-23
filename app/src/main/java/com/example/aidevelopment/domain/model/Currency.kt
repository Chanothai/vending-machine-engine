package com.example.aidevelopment.domain.model

@JvmInline
value class Currency(val amount: Int) {
    init {
        require(amount >= 0) { "Currency cannot be negative" }
    }

    operator fun plus(other: Currency) = Currency(this.amount + other.amount)
    operator fun minus(other: Currency) = Currency(this.amount - other.amount)
    operator fun compareTo(other: Currency) = this.amount.compareTo(other.amount)
}

fun Int.toCurrency() = Currency(this)

fun triple(x: Int): Int { return x * 3 }