package com.example.financeapp.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.financeapp.R
import com.example.financeapp.domain.TransactionType

@Composable
@Suppress("FunctionNaming")
fun EditorScreen(
    state: EditorState,
    onIntent: (EditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSaving = state.status == EditorStatus.SAVING

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.selectedType == TransactionType.INCOME,
                onClick = { onIntent(EditorIntent.TypeSelected(TransactionType.INCOME)) },
                label = { Text(stringResource(R.string.type_income)) }
            )
            FilterChip(
                selected = state.selectedType == TransactionType.EXPENSE,
                onClick = { onIntent(EditorIntent.TypeSelected(TransactionType.EXPENSE)) },
                label = { Text(stringResource(R.string.type_expense)) }
            )
        }

        OutlinedTextField(
            value = state.amountInput,
            onValueChange = { onIntent(EditorIntent.AmountChanged(it)) },
            label = { Text(stringResource(R.string.label_amount)) },
            isError = state.amountError != null,
            supportingText = state.amountError?.let { error -> { Text(error) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.descriptionInput,
            onValueChange = { onIntent(EditorIntent.DescriptionChanged(it)) },
            label = { Text(stringResource(R.string.label_description)) },
            isError = state.descriptionError != null,
            supportingText = state.descriptionError?.let { error -> { Text(error) } },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onIntent(EditorIntent.Save) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_save))
        }
    }
}
