package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.financeapp.data.RoomTransactionRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TransactionDatabase.open(applicationContext)
        val repository = RoomTransactionRepository(database.transactionDao())

        setContent {
            FinanceAppTheme {
                FinanceNavigation(repository = remember { repository })
            }
        }
    }
}

@Composable
@Suppress("FunctionNaming")
internal fun FinanceAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
