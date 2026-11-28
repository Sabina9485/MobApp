package com.example.financeapp.domain

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun add(transaction: Transaction)
}
