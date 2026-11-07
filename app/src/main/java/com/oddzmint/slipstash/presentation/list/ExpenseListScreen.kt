package com.oddzmint.slipstash.presentation.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.R
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.Expense
import com.oddzmint.slipstash.domain.model.SyncStatus
import com.oddzmint.slipstash.presentation.label
import com.oddzmint.slipstash.presentation.navigation.Destination
import com.oddzmint.slipstash.presentation.navigation.SlipStashBottomBar
import com.oddzmint.slipstash.ui.theme.SlipStashTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun ExpenseListScreen(
    onAddClick: () -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ExpenseListContent(
        expenses = expenses,
        onAddClick = onAddClick,
        snackbarHostState = snackbarHostState,
        onDelete = { expense ->
            viewModel.requestDelete(expense.id)
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = "Expense deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete(expense.id)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListContent(
    expenses: List<Expense>,
    onAddClick: () -> Unit,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val monthTotal = remember(expenses) { monthTotal(expenses) }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.expenses)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_expense))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp)
        ) {
            item { MonthHeader(total = monthTotal) }

            if (expenses.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_expenses_yet_Tap_to_add_one_),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    SwipeToDeleteRow(expense = expense, onDelete = { onDelete(expense) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun SwipeToDeleteRow(expense: Expense, onDelete: () -> Unit) {
    val currentOnDelete by rememberUpdatedState(onDelete)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                currentOnDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.errorContainer,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ExpenseRow(expense)
        }
    }
}

@Composable
private fun MonthHeader(total: Long) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.this_month),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAmount(total),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = expense.category.label(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            val subtitle = buildString {
                append(formatAmount(expense.amountCents))
                append(" · ")
                append(formatRelativeDate(expense.dateMillis))
                if (expense.note.isNotBlank()) append(" · ${expense.note}")
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        StatusPill(expense.syncStatus)
    }
}

@Composable
private fun StatusPill(status: SyncStatus) {
    val (color, label) = when (status) {
        SyncStatus.PENDING -> Color(0xFFE0A100) to stringResource(R.string.queued)
        SyncStatus.SYNCING -> Color(0xFF4C9AFF) to stringResource(R.string.syncing)
        SyncStatus.SYNCED -> Color(0xFF3DBA5E) to stringResource(R.string.synced)
        SyncStatus.FAILED -> Color(0xFFE5645F) to stringResource(R.string.retry)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.16f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** "R 612" for whole amounts, "R 612.50" otherwise. */
private fun formatAmount(cents: Long): String =
    if (cents % 100 == 0L) {
        String.format(Locale.getDefault(), "R %,d", cents / 100)
    } else {
        String.format(Locale.getDefault(), "R %,.2f", cents / 100.0)
    }

private fun formatRelativeDate(millis: Long): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val days = ChronoUnit.DAYS.between(date, LocalDate.now(zone))
    return when {
        days <= 0L -> "Today"
        days == 1L -> "Yesterday"
        days in 2L..6L -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
    }
}

private fun monthTotal(expenses: List<Expense>): Long {
    val zone = ZoneId.systemDefault()
    val current = YearMonth.now(zone)
    return expenses
        .filter { YearMonth.from(Instant.ofEpochMilli(it.dateMillis).atZone(zone)) == current }
        .sumOf { it.amountCents }
}

private fun sampleExpenses(): List<Expense> {
    val now = System.currentTimeMillis()
    val day = 24L * 60 * 60 * 1000
    return listOf(
        Expense(
            amountCents = 556_200,
            category = Category.GROCERIES,
            dateMillis = now,
            syncStatus = SyncStatus.PENDING,
            isDeleted = false
        ),
        Expense(
            amountCents = 250_000,
            category = Category.TRANSPORT,
            note = "Petrol",
            dateMillis = now,
            syncStatus = SyncStatus.SYNCING,
            isDeleted = false
        ),
        Expense(
            amountCents = 79_900,
            category = Category.BILLS,
            note = "Internet",
            dateMillis = now - 2 * day,
            syncStatus = SyncStatus.SYNCED,
            isDeleted = false
        ),
        Expense(
            amountCents = 921_500,
            category = Category.EATING_OUT,
            dateMillis = now - 3 * day,
            syncStatus = SyncStatus.FAILED,
            isDeleted = false
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpenseListPreview() {
    SlipStashTheme {
        ExpenseListContent(
            expenses = sampleExpenses(),
            onAddClick = {},
            onDelete = {},
        )
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExpenseListDarkThemePreview() {
    SlipStashTheme {
        ExpenseListContent(
            expenses = sampleExpenses(),
            onAddClick = {},
            onDelete = {})
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun ExpenseListEmptyPreview() {
    SlipStashTheme {
        ExpenseListContent(
            expenses = emptyList(),
            onAddClick = {},
            onDelete = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "With bottom bar")
@Composable
private fun ExpenseListWithBarPreview() {
    SlipStashTheme {
        Scaffold(
            bottomBar = {
                SlipStashBottomBar(currentRoute = Destination.Expenses.route, onTabSelected = {})
            }
        ) { padding ->
            ExpenseListContent(
                expenses = sampleExpenses(),
                onAddClick = {},
                onDelete = {},
                modifier = Modifier.padding(padding)
            )
        }
    }
}