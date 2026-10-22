package com.oddzmint.slipstash.presentation.summary

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.domain.model.CategoryTotal
import com.oddzmint.slipstash.domain.model.MonthlySummary
import com.oddzmint.slipstash.presentation.SummaryViewModel
import com.oddzmint.slipstash.presentation.color
import com.oddzmint.slipstash.presentation.formatAmount
import com.oddzmint.slipstash.presentation.label
import com.oddzmint.slipstash.presentation.model.SummaryUiState
import com.oddzmint.slipstash.ui.theme.SlipStashTheme
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SummaryScreen(
    onViewQueue: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    SummaryContent(
        state = state,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onViewQueue = onViewQueue
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryContent(
    state: SummaryUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onViewQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = state.summary
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Summary") },
                actions = {
                    MonthSwitcher(
                        month = summary.month,
                        canGoPrevious = state.canGoPrevious,
                        canGoNext = state.canGoNext,
                        onPrevious = onPreviousMonth,
                        onNext = onNextMonth
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TotalCard(summary)
            if (summary.categories.isEmpty()) {
                Text(
                    text = "No expenses in ${monthLabel(summary.month)}.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                CategorySection(summary)
            }
            Footer(summary = summary, onViewQueue = onViewQueue)
        }
    }
}

@Composable
private fun MonthSwitcher(
    month: YearMonth,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPrevious, enabled = canGoPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(
            text = monthLabel(month),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNext, enabled = canGoNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun TotalCard(summary: MonthlySummary) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Total spent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatAmount(summary.totalCents),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            val expenses = if (summary.expenseCount == 1) "1 expense" else "${summary.expenseCount} expenses"
            Text(
                text = "$expenses · ${summary.expenseCount} with receipts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (summary.categories.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                StackedBar(summary.categories)
            }
        }
    }
}

@Composable
private fun StackedBar(categories: List<CategoryTotal>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        categories.forEach { item ->
            Box(
                modifier = Modifier
                    .weight(item.amountCents.toFloat())
                    .fillMaxHeight()
                    .background(item.category.color())
            )
        }
    }
}

@Composable
private fun CategorySection(summary: MonthlySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "By category",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        summary.categories.forEach { item ->
            CategoryRow(item = item, total = summary.totalCents)
        }
    }
}

@Composable
private fun CategoryRow(item: CategoryTotal, total: Long) {
    val color = item.category.color()
    val fraction = if (total > 0) item.amountCents.toFloat() / total else 0f

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
                Spacer(Modifier.width(8.dp))
                Text(item.category.label(), style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(formatAmount(item.amountCents))
                    }
                    append(" · ${item.percentage}%")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0.02f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun Footer(summary: MonthlySummary, onViewQueue: () -> Unit) {
    Column() {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (summary.waitingCount == 0) {
                    "All synced"
                } else {
                    "${summary.syncedCount} synced, ${summary.waitingCount} waiting"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (summary.waitingCount > 0) {
                TextButton(onClick = onViewQueue) { Text("View queue") }
            }
        }
    }
}

private fun monthLabel(month: YearMonth): String {
    val name = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return if (month.year == YearMonth.now().year) name else "$name ${month.year}"
}

private fun sampleState() = SummaryUiState(
    summary = MonthlySummary(
        month = YearMonth.now(),
        totalCents = 428_000,
        expenseCount = 18,
        receiptCount = 11,
        syncedCount = 16,
        waitingCount = 2,
        categories = listOf(
            CategoryTotal(Category.GROCERIES, 164_000, 38),
            CategoryTotal(Category.BILLS, 110_000, 26),
            CategoryTotal(Category.TRANSPORT, 90_000, 21),
            CategoryTotal(Category.EATING_OUT, 42_000, 10),
            CategoryTotal(Category.ENTERTAINMENT, 22_000, 5),
            CategoryTotal(Category.OTHER, 10_000, 4)
        )
    ),
    canGoPrevious = true,
    canGoNext = false
)

@Preview(showBackground = true, name = "Light")
@Composable
private fun SummaryLightPreview() {
    SlipStashTheme {
        SummaryContent(sampleState(), onPreviousMonth = {}, onNextMonth = {}, onViewQueue = {})
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SummaryDarkPreview() {
    SlipStashTheme {
        SummaryContent(sampleState(), onPreviousMonth = {}, onNextMonth = {}, onViewQueue = {})
    }
}