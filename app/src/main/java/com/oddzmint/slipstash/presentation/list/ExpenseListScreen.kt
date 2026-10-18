package com.oddzmint.slipstash.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseListScreen(
    onAddClick: () -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    ExpenseListContent(
        expenses = expenses,
        onAddClick = onAddClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListContent(
    expenses: List<Expense>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Expenses") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->
        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expenses yet. Tap + to add one.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseRow(expense)
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category.label(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(expense.dateMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatAmount(expense.amountCents),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                StatusBadge(expense.syncStatus)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: SyncStatus) {
    val colors = MaterialTheme.colorScheme
    val (background, content, label) = when (status) {
        SyncStatus.PENDING -> Triple(colors.tertiaryContainer, colors.onTertiaryContainer, "Queued")
        SyncStatus.SYNCING -> Triple(colors.primaryContainer, colors.onPrimaryContainer, "Syncing")
        SyncStatus.SYNCED -> Triple(colors.secondaryContainer, colors.onSecondaryContainer, "Synced")
        SyncStatus.FAILED -> Triple(colors.errorContainer, colors.onErrorContainer, "Retry")
    }
    Surface(shape = RoundedCornerShape(6.dp), color = background) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

private fun Category.label(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun formatAmount(cents: Long): String =
    String.format(Locale.getDefault(), "R %,.2f", cents / 100.0)

private fun formatDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))

@Preview(showBackground = true)
@Composable
private fun ExpenseListPreview() {
    val now = System.currentTimeMillis()
    MaterialTheme {
        ExpenseListContent(
            expenses = listOf(
                Expense(
                    amountCents = 61_200,
                    category = Category.GROCERIES,
                    dateMillis = now,
                    syncStatus = SyncStatus.PENDING
                ),
                Expense(
                    amountCents = 90_000,
                    category = Category.TRANSPORT,
                    dateMillis = now,
                    syncStatus = SyncStatus.SYNCING
                ),
                Expense(
                    amountCents = 79_900,
                    category = Category.BILLS,
                    dateMillis = now,
                    syncStatus = SyncStatus.SYNCED
                ),
                Expense(
                    amountCents = 9_500,
                    category = Category.EATING_OUT,
                    dateMillis = now,
                    syncStatus = SyncStatus.FAILED
                )
            ),
            onAddClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun ExpenseListEmptyPreview() {
    MaterialTheme { ExpenseListContent(expenses = emptyList(), onAddClick = {}) }
}