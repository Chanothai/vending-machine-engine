// File: data/repository/InMemoryInventoryRepository.kt
package com.example.aidevelopment.data.repository

import com.example.aidevelopment.domain.model.Product
import com.example.aidevelopment.domain.repository.InventoryRepository

class InMemoryInventoryRepository : InventoryRepository {
    private val stock = Product.entries.associateWith { 10 }.toMutableMap()

    override fun getStock(product: Product): Int = stock[product] ?: 0

    override fun consumeProduct(product: Product) {
        val current = getStock(product)
        if (current > 0) stock[product] = current - 1
    }

    override fun setStock(product: Product, quantity: Int) {
        stock[product] = quantity
    }
}