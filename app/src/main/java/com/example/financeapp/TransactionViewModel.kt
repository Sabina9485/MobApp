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
        val amount = uiState.amount.toIntOrNull()

        when {
            uiState.amount.isEmpty() || description.isEmpty() -> {
                onEvent(MainUiEvent.InvalidTransaction)
            }

            amount == null -> {
                onEvent(MainUiEvent.AmountTooLarge)
            }

            amount <= 0 -> {
                onEvent(MainUiEvent.InvalidTransaction)
            }

            else -> {
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
    }
}
