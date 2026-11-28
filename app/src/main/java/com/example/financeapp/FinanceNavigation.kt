package com.example.financeapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.domain.TransactionRepository
import com.example.financeapp.ui.editor.EditorScreen
import com.example.financeapp.ui.editor.EditorStatus
import com.example.financeapp.ui.editor.EditorViewModel
import com.example.financeapp.ui.history.HistoryIntent
import com.example.financeapp.ui.history.HistoryScreen
import com.example.financeapp.ui.history.HistoryViewModel

private const val HISTORY_ROUTE = "history"
private const val EDITOR_ROUTE = "editor"

@Composable
@Suppress("FunctionNaming")
internal fun FinanceNavigation(repository: TransactionRepository) {
    val navController = rememberNavController()

    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                    HistoryViewModel(repository) as T
                modelClass.isAssignableFrom(EditorViewModel::class.java) ->
                    EditorViewModel(repository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = HISTORY_ROUTE
    ) {
        composable(HISTORY_ROUTE) {
            val viewModel: HistoryViewModel = viewModel(factory = factory)
            val state by viewModel.state.collectAsStateWithLifecycle()

            HistoryScreen(
                state = state,
                onRetry = { viewModel.onIntent(HistoryIntent.Retry) },
                onAddClick = {
                    navController.navigate(EDITOR_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(EDITOR_ROUTE) {
            val viewModel: EditorViewModel = viewModel(factory = factory)
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.status) {
                if (state.status == EditorStatus.SAVED) {
                    navController.popBackStack()
                }
            }

            EditorScreen(
                state = state,
                onIntent = viewModel::onIntent
            )
        }
    }
}
