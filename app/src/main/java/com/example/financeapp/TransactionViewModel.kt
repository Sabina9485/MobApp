package com.example.financeapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

internal class TransactionViewModel : ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    fun onEvent(event: MainUiEvent) {
        uiState = reduce(uiState, event)
    }

    fun addTransaction() {
        val description = uiState.description.trim()
        if (uiState.amount.isEmpty() || description.isEmpty()) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }
        val amount = uiState.amount.toIntOrNull()
        if (amount == null) {
            onEvent(MainUiEvent.AmountTooLarge)
            return
        }
        if (amount <= 0) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }
        onEvent(
            MainUiEvent.AddTransaction(
                Transaction(
                    amount = amount,
                    type = uiState.type,
                    description = description,
                )
            )
        )
    }
}
