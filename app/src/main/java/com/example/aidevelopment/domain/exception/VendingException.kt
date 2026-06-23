package com.example.aidevelopment.domain.exception

import com.example.aidevelopment.domain.model.Coin

sealed class VendingException(message: String) : Exception(message) {

    class OutOfStockException : VendingException("Product is out of stock.")

    class InsufficientFundsException : VendingException("Insufficient funds for purchase.")

    data class NoChangeAvailableException(val refund: List<Coin>) :
        VendingException("Machine cannot provide exact change. Transaction aborted.")

    class InvalidCoinException : VendingException("The machine only accepts 1, 5, and 10 coins.")

    class InvalidProductException : VendingException("Invalid product selected.")
}
