package com.oddzmint.slipstash.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oddzmint.slipstash.ui.theme.SlipStashTheme

internal enum class TopLevelTab(val destination: Destination, val label: String) {
    EXPENSES(Destination.Expenses, "Expenses"),
    SUMMARY(Destination.Summary, "Summary")
}

@Composable
fun SlipStashBottomBar(
    currentRoute: String?,
    onTabSelected: (Destination) -> Unit
) {
    NavigationBar {
        TopLevelTab.entries.forEach { tab ->
            val selected = currentRoute == tab.destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelected(tab.destination) },
                icon = { TabIcon(tab) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
private fun TabIcon(tab: TopLevelTab) {
    when (tab) {
        TopLevelTab.EXPENSES -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
        TopLevelTab.SUMMARY -> BarsIcon()
    }
}

@Composable
private fun BarsIcon() {
    val color = LocalContentColor.current
    Row(
        modifier = Modifier
            .size(24.dp)
            .padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(10.dp, 18.dp, 14.dp).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true, name = "Expense selected")
@Composable
private fun BottomBarExpensesPreview() {
    SlipStashTheme {
        SlipStashBottomBar(currentRoute = Destination.Expenses.route, onTabSelected = {})
    }
}

@Preview(showBackground = true, name = "Summary selected")
@Composable
private fun BottomBarSummaryPreview() {
    SlipStashTheme {
        SlipStashBottomBar(currentRoute = Destination.Summary.route, onTabSelected = {})
    }
}