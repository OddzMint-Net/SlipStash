package com.oddzmint.slipstash.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.presentation.label
import com.oddzmint.slipstash.ui.theme.SlipStashTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selected: Category,
    onSelect: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        AppTextField(
            label = "Category",
            value = selected.label(),
            onValueChange = {},
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.label()) },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun Category.label(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }


@Preview(showBackground = true)
@Composable
private fun CategoryDropdownPreview() {
    SlipStashTheme {

        var selected by remember { mutableStateOf(Category.GROCERIES) }
        Box(modifier = Modifier.padding(16.dp))
        CategoryDropdown(
            selected = selected,
            onSelect = { selected = it }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoryDropdownDarkPreview() {
    SlipStashTheme {

        var selected by remember { mutableStateOf(Category.BILLS) }
        Box(modifier = Modifier.padding(16.dp))
        CategoryDropdown(
            selected = selected,
            onSelect = { selected = it }
        )
    }
}