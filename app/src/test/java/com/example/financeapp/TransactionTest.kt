package com.example.financeapp

import com.example.financeapp.domain.Transaction
import com.example.financeapp.domain.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionTest {

    @Test
    fun balanceCalculationHandlesLargeIncomesWithoutOverflow() {
        val transactions = listOf(
            Transaction(
                id = 1L,
                amount = Int.MAX_VALUE.toLong(),
                type = TransactionType.INCOME,
                description = "Доход 1"
            ),
            Transaction(
                id = 2L,
                amount = Int.MAX_VALUE.toLong(),
                type = TransactionType.INCOME,
                description = "Доход 2"
            ),
            Transaction(
                id = 3L,
                amount = 1L,
                type = TransactionType.EXPENSE,
                description = "Расход"
            )
        )

        val balance = transactions.sumOf(Transaction::signedAmount)

        assertEquals(4_294_967_293L, balance)
    }
}
