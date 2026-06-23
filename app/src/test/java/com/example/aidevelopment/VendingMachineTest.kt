package com.example.aidevelopment

import com.example.aidevelopment.data.repository.InMemoryInventoryRepository
import com.example.aidevelopment.data.repository.InMemoryVaultRepository
import com.example.aidevelopment.domain.exception.VendingException
import com.example.aidevelopment.domain.model.Coin
import com.example.aidevelopment.domain.model.Currency
import com.example.aidevelopment.domain.model.Product
import com.example.aidevelopment.domain.repository.InventoryRepository
import com.example.aidevelopment.domain.repository.VaultRepository
import com.example.aidevelopment.domain.service.ChangeCalculator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Vending Machine Core Logic Tests")
class VendingMachineTest {

    private lateinit var inventory: InventoryRepository
    private lateinit var vault: VaultRepository
    private lateinit var changeCalculator: ChangeCalculator
    private lateinit var vendingMachine: VendingMachine

    @BeforeEach
    fun setUp() {
        inventory = mockk(relaxed = true)
        vault = mockk(relaxed = true)
        changeCalculator = mockk()
        vendingMachine = VendingMachine(inventory, vault, changeCalculator)
    }

    @Nested
    @DisplayName("insertCoin() tests")
    inner class InsertCoinTests {

        @Test
        @DisplayName("Should increase balance when valid coins are inserted")
        fun insertValidCoins() {
            vendingMachine.insertCoin(1)
            vendingMachine.insertCoin(5)
            vendingMachine.insertCoin(10)

            assertThat(vendingMachine.currentBalance).isEqualTo(16)
        }

        @Test
        @DisplayName("Should throw InvalidCoinException for invalid denominations")
        fun insertInvalidCoin() {
            assertThatThrownBy { vendingMachine.insertCoin(2) }
                .isInstanceOf(VendingException.InvalidCoinException::class.java)
        }
    }

    @Nested
    @DisplayName("selectProduct() tests")
    inner class SelectProductTests {

        @Test
        @DisplayName("Should dispense product and return change when conditions are met")
        fun successfulPurchase() {
            // Setup
            val product = Product.COLA // Price 15
            vendingMachine.insertCoin(10)
            vendingMachine.insertCoin(10) // Total 20
            
            every { inventory.getStock(product) } returns 5
            every { vault.getSnapshot() } returns mapOf(Coin.FIVE to 10)
            every { changeCalculator.calculateChange(Currency(5), any()) } returns listOf(Coin.FIVE)

            // Execute
            val result = vendingMachine.selectProduct("COLA")

            // Verify
            assertThat(result).isEqualTo("Dispensed COLA. Change: 5")
            verify { inventory.consumeProduct(product) }
            verify { vault.addCoins(match { it.size == 2 && it.all { c -> c == Coin.TEN } }) }
            verify { vault.removeCoins(listOf(Coin.FIVE)) }
            assertThat(vendingMachine.currentBalance).isEqualTo(0)
        }

        @Test
        @DisplayName("Should throw OutOfStockException when product is unavailable")
        fun productOutOfStock() {
            vendingMachine.insertCoin(10)
            vendingMachine.insertCoin(10)
            
            every { inventory.getStock(Product.COLA) } returns 0

            assertThatThrownBy { vendingMachine.selectProduct("COLA") }
                .isInstanceOf(VendingException.OutOfStockException::class.java)
        }

        @Test
        @DisplayName("Should throw InsufficientFundsException when balance is too low")
        fun insufficientFunds() {
            vendingMachine.insertCoin(10)
            
            every { inventory.getStock(Product.COLA) } returns 5

            assertThatThrownBy { vendingMachine.selectProduct("COLA") }
                .isInstanceOf(VendingException.InsufficientFundsException::class.java)
        }

        @Test
        @DisplayName("Should abort purchase and refund if exact change cannot be made")
        fun noChangeAvailable() {
            // Setup: Buy Water (10) with 15 (inserted 10 + 5)
            vendingMachine.insertCoin(10)
            vendingMachine.insertCoin(5)
            
            every { inventory.getStock(Product.WATER) } returns 5
            every { vault.getSnapshot() } returns emptyMap()
            // Change needed is 5, but vault is empty
            every { changeCalculator.calculateChange(Currency(5), any()) } returns null

            // Execute & Verify
            assertThatThrownBy { vendingMachine.selectProduct("WATER") }
                .isInstanceOf(VendingException.NoChangeAvailableException::class.java)
                .extracting("refund")
                .isEqualTo(listOf(Coin.TEN, Coin.FIVE))

            assertThat(vendingMachine.currentBalance).isEqualTo(0)
            verify(exactly = 0) { inventory.consumeProduct(any()) }
        }

        @Test
        @DisplayName("Should throw InvalidProductException if product name is unknown")
        fun invalidProductName() {
            vendingMachine.insertCoin(10)
            assertThatThrownBy { vendingMachine.selectProduct("PIZZA") }
                .isInstanceOf(VendingException.InvalidProductException::class.java)
        }
    }

    @Nested
    @DisplayName("cancelRequest() tests")
    inner class CancelRequestTests {

        @Test
        @DisplayName("Should refund full balance and reset to Idle state")
        fun cancelTransaction() {
            vendingMachine.insertCoin(10)
            vendingMachine.insertCoin(5)
            
            val refund = vendingMachine.cancelRequest()

            assertThat(refund).isEqualTo(15)
            assertThat(vendingMachine.currentBalance).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("ChangeCalculator Coverage Tests")
    inner class ChangeCalculatorTests {
        private val calculator = ChangeCalculator()

        @Test
        @DisplayName("GIVEN amount is 0 THEN return empty list immediately")
        fun amountZero() {
            // Exercises: if (amountNeeded.amount == 0) return emptyList()
            assertThat(calculator.calculateChange(Currency(0), emptyMap())).isEmpty()
        }

        @Test
        @DisplayName("GIVEN sufficient vault supply THEN return optimal coin combination using greedy approach")
        fun optimalSuccess() {
            val vault = mapOf(Coin.TEN to 1, Coin.FIVE to 2, Coin.ONE to 5)

            // Exercises: while loop entered multiple times for same/different denominations
            // Case 1: 16 = 10 + 5 + 1
            assertThat(calculator.calculateChange(Currency(16), vault))
                .containsExactly(Coin.TEN, Coin.FIVE, Coin.ONE)

            // Case 2: 10 = 5 + 5
            assertThat(calculator.calculateChange(Currency(10), mapOf(Coin.FIVE to 2)))
                .containsExactly(Coin.FIVE, Coin.FIVE)
        }

        @Test
        @DisplayName("GIVEN vault is missing denominations or out of stock THEN skip and attempt others")
        fun skipUnavailableCoins() {
            val vault = mapOf(
                Coin.TEN to 0, // Out of stock branch: (tempVault[coin] ?: 0) > 0 is false
                Coin.FIVE to 1 // ONE is missing from map: (tempVault[coin] ?: 0) defaults to 0
            )

            // Attempt 10: Skips TEN (stock), uses FIVE (stock 1), fails for remaining 5 (no ONE)
            assertThat(calculator.calculateChange(Currency(10), vault)).isNull()

            // Attempt 4: Skips TEN and FIVE (remaining >= coin.value.amount is false)
            assertThat(calculator.calculateChange(Currency(4), vault)).isNull()
        }

        @Test
        @DisplayName("GIVEN greedy algorithm cannot reach zero THEN return null")
        fun remainingNonZero() {
            val vault = mapOf(Coin.TEN to 5)
            // Exercises: return if (remaining == 0) result else null (the else branch)
            assertThat(calculator.calculateChange(Currency(5), vault)).isNull()
        }
    }

    @Nested
    @DisplayName("InMemoryInventoryRepository Coverage Tests")
    inner class InventoryRepositoryTests {
        private val repository = InMemoryInventoryRepository()

        @Test
        @DisplayName("GIVEN product stock WHEN setStock is called THEN getStock returns new value")
        fun setAndGetStock() {
            repository.setStock(Product.COLA, 42)
            assertThat(repository.getStock(Product.COLA)).isEqualTo(42)
        }

        @Test
        @DisplayName("GIVEN product has stock WHEN consumeProduct THEN stock decreases")
        fun consumeAvailableStock() {
            repository.setStock(Product.WATER, 1)
            repository.consumeProduct(Product.WATER)
            assertThat(repository.getStock(Product.WATER)).isZero()
        }

        @Test
        @DisplayName("GIVEN product is out of stock WHEN consumeProduct THEN stock remains zero")
        fun consumeEmptyStock() {
            // Branch coverage: if (current > 0) is false
            repository.setStock(Product.SNACK, 0)
            repository.consumeProduct(Product.SNACK)
            assertThat(repository.getStock(Product.SNACK)).isZero()
        }
    }

    @Nested
    @DisplayName("InMemoryVaultRepository Coverage Tests")
    inner class VaultRepositoryTests {
        private val repository = InMemoryVaultRepository()

        @Test
        @DisplayName("GIVEN coins WHEN addCoins THEN coin count increases")
        fun addCoins() {
            val initial = repository.getCoinCount(Coin.TEN)
            repository.addCoins(listOf(Coin.TEN, Coin.TEN))
            assertThat(repository.getCoinCount(Coin.TEN)).isEqualTo(initial + 2)
        }

        @Test
        @DisplayName("GIVEN vault has coins WHEN removeCoins THEN count decreases")
        fun removeAvailableCoins() {
            repository.addCoins(listOf(Coin.FIVE))
            val before = repository.getCoinCount(Coin.FIVE)
            repository.removeCoins(listOf(Coin.FIVE))
            assertThat(repository.getCoinCount(Coin.FIVE)).isEqualTo(before - 1)
        }

        @Test
        @DisplayName("GIVEN vault is empty of a denomination WHEN removeCoins THEN count remains zero")
        fun removeEmptyCoins() {
            // Branch coverage: if (current > 0) is false
            val current = repository.getCoinCount(Coin.ONE)
            repeat(current) { repository.removeCoins(listOf(Coin.ONE)) }

            repository.removeCoins(listOf(Coin.ONE))
            assertThat(repository.getCoinCount(Coin.ONE)).isZero()
        }

        @Test
        @DisplayName("GIVEN vault state WHEN getSnapshot THEN return immutable map of all denominations")
        fun snapshotVerification() {
            val snapshot = repository.getSnapshot()
            assertThat(snapshot).containsKeys(Coin.ONE, Coin.FIVE, Coin.TEN)
            assertThat(snapshot[Coin.TEN]).isEqualTo(repository.getCoinCount(Coin.TEN))
        }
    }

}
