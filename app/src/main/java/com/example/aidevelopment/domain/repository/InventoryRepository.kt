package com.example.aidevelopment.domain.repository

import com.example.aidevelopment.domain.model.Product

interface InventoryRepository {
    fun getStock(product: Product): Int
    fun consumeProduct(product: Product)
    fun setStock(product: Product, quantity: Int)
}