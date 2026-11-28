package com.example.financeapp.legacy

import android.content.Context
import com.example.financeapp.Transaction
import com.example.financeapp.TransactionDatabase
import com.example.financeapp.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class TransactionRepository(context: Context) {
    private val database = TransactionDatabase.open(context)
    private val dao = database.transactionDao()

    fun load(onResult: (List<Transaction>?) -> Unit) {
        val transactions = runBlocking {
            dao.observeAll().first().map(TransactionEntity::toTransaction)
        }
        onResult(transactions)
    }

    fun save(transaction: Transaction, onResult: (Boolean) -> Unit) {
        runBlocking {
            dao.insert(TransactionEntity.fromTransaction(transaction))
        }
        onResult(true)
    }

    fun close() {
        database.close()
    }
}
