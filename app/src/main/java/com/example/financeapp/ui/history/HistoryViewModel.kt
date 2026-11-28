package com.example.financeapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface HistoryState {
    data object Loading : HistoryState
    data class Ready(
        val transactions: List<Transaction>,
        val balance: Long
    ) : HistoryState
    data class LoadFailed(val message: String) : HistoryState
}

sealed interface HistoryIntent {
    data object Retry : HistoryIntent
}

class HistoryViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val state: StateFlow<HistoryState> = mutableState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeHistory()
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.Retry -> observeHistory()
        }
    }

    private fun observeHistory() {
        observeJob?.cancel()
        mutableState.value = HistoryState.Loading
        observeJob = viewModelScope.launch {
            repository.observeTransactions()
                .catch { error ->
                    mutableState.value = HistoryState.LoadFailed(
                        error.message ?: "Ошибка загрузки"
                    )
                }
                .collect { transactions ->
                    val balance = transactions.sumOf(Transaction::signedAmount)
                    mutableState.value = HistoryState.Ready(transactions, balance)
                }
        }
    }
}
