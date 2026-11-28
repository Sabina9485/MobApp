package com.example.financeapp.data

import com.example.financeapp.TransactionDao
import com.example.financeapp.TransactionEntity
import com.example.financeapp.TransactionType as DataTransactionType
import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.domain.TransactionType as DomainTransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomTransactionRepository(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun observeTransactions(): Flow<List<Transaction>> {
        return dao.observeAll().map { entities ->
            entities.map { entity ->
                Transaction(
                    id = entity.id,
                    amount = entity.amount.toLong(),
                    type = when (entity.type) {
                        DataTransactionType.INCOME -> DomainTransactionType.INCOME
                        DataTransactionType.EXPENSE -> DomainTransactionType.EXPENSE
                    },
                    description = entity.description
                )
            }
        }
    }

    override suspend fun add(transaction: Transaction) {
        val entity = TransactionEntity(
            id = transaction.id,
            amount = transaction.amount.toInt(),
            type = when (transaction.type) {
                DomainTransactionType.INCOME -> DataTransactionType.INCOME
                DomainTransactionType.EXPENSE -> DataTransactionType.EXPENSE
            },
            description = transaction.description
        )
        dao.insert(entity)
    }
}
