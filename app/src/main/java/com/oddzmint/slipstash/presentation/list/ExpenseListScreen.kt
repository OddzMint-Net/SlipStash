package com.oddzmint.slipstash.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseListScreen(
    onAddClick: () -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    ExpenseListContent(expenses = expenses, onAddClick = onAddClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListContent(
    expenses: List<Expense>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (monthTotal, monthCount) = remember(expenses) { monthSummary(expenses) }
    val waiting = remember(expenses) { expenses.count { it.syncStatus != SyncStatus.SYNCED } }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Expenses") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp)
        ) {
            item {
                SummaryHeader(total = monthTotal, count = monthCount, waiting = waiting)
            }
            if (expenses.isEmpty()) {
                item {
                    Text(
                        text = "No expenses yet. Tap + to add one.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseRow(expense)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(total: Long, count: Int, waiting: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "This month",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAmount(total),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium
            )
            val waitingText = if (waiting == 0) "all synced" else "$waiting waiting to sync"
            Text(
                text = "$count expenses · $waitingText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(expense.category.color())
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.category.label(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            val subtitle = buildString {
                append(formatDate(expense.dateMillis))
                if (expense.note.isNotBlank()) append(" · ${expense.note}")
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = formatAmount(expense.amountCents),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            StatusBadge(expense.syncStatus)
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

private fun Category.color(): Color = when (this) {
    Category.GROCERIES -> Color(0xFF378ADD)
    Category.BILLS -> Color(0xFF1D9E75)
    Category.TRANSPORT -> Color(0xFFD85A30)
    Category.EATING_OUT -> Color(0xFFDD77A0)
    Category.ENTERTAINMENT ->Color(0xFFDE20B0)
    Category.OTHER -> Color(0xFFEED20E)
}

private fun Category.label(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun formatAmount(cents: Long): String =
    String.format(Locale.getDefault(), "R %,.2f", cents / 100.0)

private fun formatDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))

private fun monthSummary(expenses: List<Expense>): Pair<Long, Int> {
    val zone = ZoneId.systemDefault()
    val current = YearMonth.now(zone)
    val thisMonth = expenses.filter {
        YearMonth.from(Instant.ofEpochMilli(it.dateMillis).atZone(zone)) == current
    }
    return thisMonth.sumOf { it.amountCents } to thisMonth.size
}

@Preview(showBackground = true)
@Composable
private fun ExpenseListPreview() {
    val now = System.currentTimeMillis()
    MaterialTheme {
        ExpenseListContent(
            expenses = listOf(
                Expense(amountCents = 61_200, category = Category.GROCERIES, note = "Weekly shop", dateMillis = now, syncStatus = SyncStatus.PENDING),
                Expense(amountCents = 90_000, category = Category.TRANSPORT, dateMillis = now, syncStatus = SyncStatus.SYNCING),
                Expense(amountCents = 79_900, category = Category.BILLS, note = "Internet", dateMillis = now, syncStatus = SyncStatus.SYNCED),
                Expense(amountCents = 9_500, category = Category.EATING_OUT, dateMillis = now, syncStatus = SyncStatus.FAILED)
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