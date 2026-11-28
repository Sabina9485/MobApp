package com.example.financeapp.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.domain.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EditorStatus {
    EDITING,
    SAVING,
    SAVED,
    SAVE_FAILED
}

data class EditorState(
    val amountInput: String = "",
    val descriptionInput: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val status: EditorStatus = EditorStatus.EDITING,
    val amountError: String? = null,
    val descriptionError: String? = null
)

sealed interface EditorIntent {
    data class AmountChanged(val value: String) : EditorIntent
    data class DescriptionChanged(val value: String) : EditorIntent
    data class TypeSelected(val type: TransactionType) : EditorIntent
    data object Save : EditorIntent
}

class EditorViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val mutableState = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = mutableState.asStateFlow()

    fun onIntent(intent: EditorIntent) {
        when (intent) {
            is EditorIntent.AmountChanged -> {
                mutableState.update {
                    it.copy(
                        amountInput = intent.value,
                        amountError = null,
                        status = if (it.status == EditorStatus.SAVE_FAILED) EditorStatus.EDITING else it.status
                    )
                }
            }

            is EditorIntent.DescriptionChanged -> {
                mutableState.update {
                    it.copy(
                        descriptionInput = intent.value,
                        descriptionError = null,
                        status = if (it.status == EditorStatus.SAVE_FAILED) EditorStatus.EDITING else it.status
                    )
                }
            }

            is EditorIntent.TypeSelected -> {
                mutableState.update { it.copy(selectedType = intent.type) }
            }

            EditorIntent.Save -> saveTransaction()
        }
    }

    private fun saveTransaction() {
        val current = mutableState.value
        if (current.status == EditorStatus.SAVING) return

        val parsedAmount = current.amountInput.trim().toIntOrNull()
        val trimmedDescription = current.descriptionInput.trim()

        val amountError = if (parsedAmount == null || parsedAmount <= 0) {
            "Введите сумму больше нуля"
        } else {
            null
        }

        val descriptionError = if (trimmedDescription.isEmpty()) {
            "Введите описание операции"
        } else {
            null
        }

        if (amountError != null || descriptionError != null) {
            mutableState.update {
                it.copy(
                    amountError = amountError,
                    descriptionError = descriptionError
                )
            }
            return
        }

        mutableState.update { it.copy(status = EditorStatus.SAVING) }

        viewModelScope.launch {
            runCatching {
                val transaction = Transaction(
                    amount = parsedAmount!!.toLong(),
                    type = current.selectedType,
                    description = trimmedDescription
                )
                repository.add(transaction)
            }.onSuccess {
                mutableState.update { it.copy(status = EditorStatus.SAVED) }
            }.onFailure {
                mutableState.update { it.copy(status = EditorStatus.SAVE_FAILED) }
            }
        }
    }
}
