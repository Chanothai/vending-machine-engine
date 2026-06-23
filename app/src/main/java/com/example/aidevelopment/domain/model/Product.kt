package com.example.aidevelopment.domain.model

enum class Product(val id: String, val price: Currency) {
    COLA("COLA", Currency(15)),
    WATER("WATER", Currency(10)),
    SNACK("SNACK", Currency(12)),
    ;

    companion object {
        fun fromId(id: String): Product =
            Product.entries.find { it.id.equals(id, ignoreCase = true) }
                ?: throw IllegalArgumentException("Product not found: $id")
    }
}
