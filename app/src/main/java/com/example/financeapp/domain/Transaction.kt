package com.example.financeapp.domain

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Transaction(
    val id: Long = 0L,
    val amount: Long,
    val type: TransactionType,
    val description: String
) {
    init {
        require(amount > 0L) { "Сумма должна быть положительной" }
    }

    val signedAmount: Long
        get() = when (type) {
            TransactionType.INCOME -> amount
            TransactionType.EXPENSE -> -amount
        }
}
