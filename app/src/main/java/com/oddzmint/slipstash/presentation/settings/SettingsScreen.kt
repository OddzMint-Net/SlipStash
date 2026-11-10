package com.oddzmint.slipstash.presentation.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.ui.theme.SlipStashTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    SettingsContent(state = state, onDeleteConfirmed = viewModel::deleteEverything)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SlipStash saves your expenses and receipt photos on this phone first, and then syncs them to the developer's Firebase project under an anonymous ID. There is no account, email or name.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "You can delete everything at any time. This removes all expenses and receipts from this phone and from the cloud, and deletes the anonymous ID.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showDialog = true },
                enabled = !state.isDeleting,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                if (state.isDeleting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Deleting...")
                    }
                } else {
                    Text("Deleting all my data", color = MaterialTheme.colorScheme.error)
                }
            }
            state.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete all your data?") },
            text = {
                Text(
                    "This permanently deletes all your expenses and receipt photos from phone and from the cloud. This can't be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDeleteConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError

                    )
                ) { Text("Delete everything") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Preview(showBackground = true, name = "Idle")
@Composable
private fun SettingsPreview() {
    SlipStashTheme {
        SettingsContent(
            SettingsUiState(), onDeleteConfirmed = {}
        )
    }
}

@Preview(showBackground = true, name = "Deleting")
@Composable
private fun SettingsDeletingPreview() {
    SlipStashTheme {
        SettingsContent(
            SettingsUiState(isDeleting = true), onDeleteConfirmed = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun SettingsErrorPreview() {
    SlipStashTheme {
        SettingsContent(
            SettingsUiState(
                message = "Couldn't delete your data. Check your connection and try again. Nothing was removed from this phone.",
                isError = true
            ),
            onDeleteConfirmed = {}
        )
    }
}

@Preview(showBackground = true, name = "Done")
@Composable
private fun SettingsDonePreview() {
    SlipStashTheme {
        SettingsContent(SettingsUiState(message = "All your data has been deleted."), onDeleteConfirmed = {})
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsDarkPreview() {
    SlipStashTheme {
        SettingsContent(SettingsUiState(), onDeleteConfirmed = {})
    }
}