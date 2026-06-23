# 🥤 VendingMachine Core: Domain-Driven State Engine

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-100%25-success)
![Architecture](https://img.shields.io/badge/architecture-Clean%20%7C%20DDD-orange)
![Testing](https://img.shields.io/badge/testing-JUnit5%20%7C%20MockK%20%7C%20AssertJ-blue)

> An enterprise-grade Android domain logic engine simulating a Vending Machine. Built strictly with Test-Driven Development (TDD) to showcase Clean Architecture, robust state management, and algorithmic problem-solving.

## 🎯 Project Overview
โปรเจกต์นี้ไม่ใช่แค่แอปตู้จำหน่ายสินค้าธรรมดา แต่ถูกออกแบบมาเพื่อเป็น **"Showcase ของ Core Business Logic"** ที่แยกขาดจาก UI (Presentation Layer) อย่างสิ้นเชิง ระบบถูกสร้างขึ้นมาเพื่อพิสูจน์ความสามารถในการจัดการ State ที่ซับซ้อน, การจัดการคลังสินค้า (Inventory), และระบบคำนวณเงินสด (Vault & Change Calculation) ภายใต้สภาวะ Edge Cases ต่างๆ

## 🧠 Core Engineering Highlights (ทำไมโปรเจกต์นี้ถึงพิเศษ?)

### 1. The Greedy Change Algorithm
ระบบทอนเงิน (`ChangeCalculator`) ไม่ได้ใช้การลบเลขธรรมดา แต่ใช้ **Greedy Algorithm** ในการคำนวณเหรียญทอน (`Coin.TEN`, `Coin.FIVE`, `Coin.ONE`) โดยทำงานร่วมกับสถานะของ `VaultRepository` (คลังเหรียญในตู้) แบบ Real-time
* *Smart Fallback:* หากตู้มีเหรียญไม่พอทอนให้พอดี ระบบจะไม่หักเงินผู้ใช้ แต่จะโยน `NoChangeAvailableException` พร้อมกับ **Refund (คืนเงินเต็มจำนวน)** กลับไปให้ทันที เพื่อรับประกัน Data Integrity

### 2. Impeccable Exception Handling
ระบบถูกออกแบบโดยยึดหลัก "Fail-Fast" ผ่าน Custom Exceptions ที่ครอบคลุมทุก Business Rules:
* `InvalidCoinException`: ป้องกันการหยอดเหรียญแปลกปลอม
* `OutOfStockException`: ล็อกการทำ Transaction หาก `Inventory` ว่างเปล่า
* `InsufficientFundsException`: ตรวจสอบ Balance ก่อนจ่ายสินค้า
* `InvalidProductException`: ป้องกันการยิง Request สินค้าที่ไม่มีในระบบ

### 3. TDD & 100% Domain Test Coverage
หัวใจของโปรเจกต์นี้คือความเสถียร โค้ดถูกเขียนขึ้นโดยอิงจาก Test Cases (Behavior-Driven) โดยใช้เครื่องมือระดับอุตสาหกรรม:
* **JUnit 5** (Nested Tests สำหรับการจัดกลุ่ม Context)
* **MockK** (สำหรับการ Mock Repositories)
* **AssertJ** (Fluent assertions สำหรับตรวจสอบผลลัพธ์)

## 🏗️ Architectural Blueprint

ระบบยึดหลัก **SOLID Principles** และ **Clean Architecture**:

* `VendingMachine` (Facade/Engine): ทำหน้าที่เป็น Orchestrator จัดการ Flow ทั้งหมด
* `InventoryRepository`: จัดการ Stock สินค้า (`Product.COLA`, `Product.WATER`, `Product.SNACK`)
* `VaultRepository`: จัดการเรื่องเหรียญเข้า-ออก และการทำ State Snapshot
* `ChangeCalculator`: Domain Service บริสุทธิ์ที่ไม่มี State เป็นของตัวเอง (Pure Function) ทำให้เทสต์ได้ง่าย

## 🚦 Transaction Flow Example (Successful Purchase)
1. User calls `insertCoin(10)` -> VendingMachine validates & updates `currentBalance`.
2. User calls `selectProduct("COLA")`.
3. Engine checks `InventoryRepository` (Stock > 0).
4. Engine calls `ChangeCalculator` against current `VaultSnapshot`.
5. Engine consumes product, adds inserted coins to `Vault`, dispenses change, and resets balance.

## 🛠️ Next Steps / Roadmap
* **[Phase 2]** Implement Coroutines & Flow (StateFlow) for asynchronous transaction handling.
* **[Phase 3]** UI Layer Implementation using Jetpack Compose (MVI Pattern).
* **[Phase 4]** Replace `InMemory` Repositories with Room Database (Local Persistence).