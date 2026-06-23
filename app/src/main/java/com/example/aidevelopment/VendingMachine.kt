package com.example.aidevelopment

import com.example.aidevelopment.data.repository.InMemoryInventoryRepository
import com.example.aidevelopment.data.repository.InMemoryVaultRepository
import com.example.aidevelopment.domain.exception.VendingException
import com.example.aidevelopment.domain.model.Coin
import com.example.aidevelopment.domain.model.Product
import com.example.aidevelopment.domain.model.VendingState
import com.example.aidevelopment.domain.repository.InventoryRepository
import com.example.aidevelopment.domain.repository.VaultRepository
import com.example.aidevelopment.domain.service.ChangeCalculator

class VendingMachine(
    private val inventory: InventoryRepository = InMemoryInventoryRepository(),
    private val vault: VaultRepository = InMemoryVaultRepository(),
    private val changeCalculator: ChangeCalculator = ChangeCalculator()
) {
    private var state: VendingState = VendingState.Idle

    val currentBalance: Int
        get() = when (val s = state) {
            is VendingState.HasBalance -> s.balance.amount
            VendingState.Idle -> 0
        }

    fun insertCoin(amount: Int) {
        val coin = try {
            Coin.fromInt(amount)
        } catch (e: IllegalArgumentException) {
            throw VendingException.InvalidCoinException()
        }

        state = when (val s = state) {
            VendingState.Idle -> VendingState.HasBalance(coin.value, listOf(coin))
            is VendingState.HasBalance -> VendingState.HasBalance(s.balance + coin.value, s.insertedCoins + coin)
        }
    }

    fun selectProduct(productName: String): String {
        val currentState = state as? VendingState.HasBalance
            ?: throw VendingException.InvalidProductException()

        val product = try {
            Product.fromId(productName)
        } catch (e: IllegalArgumentException) {
            throw VendingException.InvalidProductException()
        }

        if (inventory.getStock(product) <= 0) {
            throw VendingException.OutOfStockException()
        }

        if (currentState.balance < product.price) {
            throw VendingException.InsufficientFundsException()
        }

        val changeNeeded = currentState.balance - product.price
        val changeCoins = changeCalculator.calculateChange(changeNeeded, vault.getSnapshot())

        if (changeCoins == null) {
            val refundCoins = currentState.insertedCoins
            state = VendingState.Idle
            throw VendingException.NoChangeAvailableException(refundCoins)
        }

        inventory.consumeProduct(product)
        vault.addCoins(currentState.insertedCoins)
        vault.removeCoins(changeCoins)

        state = VendingState.Idle

        val changeSummary = if (changeCoins.isEmpty()) "0" else changeCoins.map { it.value.amount }.joinToString(",")
        return "Dispensed ${product.id}. Change: $changeSummary"
    }

    fun cancelRequest(): Int {
        val refund = currentBalance
        state = VendingState.Idle
        return refund
    }
}