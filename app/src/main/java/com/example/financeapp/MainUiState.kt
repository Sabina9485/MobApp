package com.example.financeapp

internal data class MainUiState(
    val balance: Int = 25_000,
    val transactionCount: Int = 0,
)

internal enum class TransactionType {
    INCOME, EXPENSE
}

internal data class Transaction(
    val amount: Int,
    val type: TransactionType,
)

internal sealed interface MainUiEvent {
    data class AddTransaction(val transaction: Transaction) : MainUiEvent
}

internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val transaction = event.transaction

        require(transaction.amount > 0)

        val delta = when (transaction.type) {
            TransactionType.INCOME -> transaction.amount
            TransactionType.EXPENSE -> -transaction.amount
        }

        state.copy(
            balance = state.balance + delta,
            transactionCount = state.transactionCount + 1
        )
    }
}

