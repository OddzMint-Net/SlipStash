package com.oddzmint.slipstash.presentation.add

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.presentation.components.AppTextField
import com.oddzmint.slipstash.presentation.components.CategoryDropdown
import com.oddzmint.slipstash.presentation.components.PrimaryActionButton
import com.oddzmint.slipstash.presentation.components.ReceiptArea
import com.oddzmint.slipstash.presentation.model.AddExpenseUiState
import com.oddzmint.slipstash.ui.theme.SlipStashTheme
import java.io.File
import java.util.*

@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // The photo file is created before the camera opens; remember its path across rotation
    var pendingPath by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val path = pendingPath
        if (success && path != null) {
            viewModel.onReceiptCaptured(path)
        } else if (path != null) {
            File(path).delete()   // user cancelled: remove the empty file
        }
        pendingPath = null
    }

    fun takePhoto() {
        val file = File(context.filesDir, "receipts/${UUID.randomUUID()}.jpg")
        file.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", file
        )
        pendingPath = file.absolutePath
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    AddExpenseContent(
        state = state,
        onAmountChange = viewModel::onAmountChange,
        onCategoryChange = viewModel::onCategoryChange,
        onNoteChange = viewModel::onNoteChange,
        onTakePhoto = ::takePhoto,
        onRemovePhoto = viewModel::onReceiptRemoved,
        onSave = viewModel::onSave
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseContent(
    state: AddExpenseUiState,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onNoteChange: (String) -> Unit,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add expense") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReceiptArea(
                path = state.receiptPath,
                onTakePhoto = onTakePhoto,
                onRemovePhoto = onRemovePhoto
            )

            AppTextField(
                label = "Amount",
                value = state.amountText,
                onValueChange = onAmountChange,
                prefix = "R ",
                placeholder = "0.00",
                errorText = state.error,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            CategoryDropdown(
                selected = state.category,
                onSelect = onCategoryChange
            )

            AppTextField(
                label = "Note (optional)",
                value = state.note,
                onValueChange = onNoteChange
            )

            PrimaryActionButton(
                text = "Save",
                onClick = onSave
            )

            Text(
                text = "Saved on device first, synced later",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun Category.label(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

@Preview(showBackground = true)
@Composable
private fun AddExpensePreview() {
    SlipStashTheme {
        AddExpenseContent(
            state = AddExpenseUiState(amountText = "612.00"),
            onAmountChange = {}, onCategoryChange = {}, onNoteChange = {},
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddExpenseDarkThemePreview() {
    SlipStashTheme {
        AddExpenseContent(
            state = AddExpenseUiState(amountText = "720.00"),
            onAmountChange = {}, onCategoryChange = {}, onNoteChange = {},
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun AddExpenseErrorPreview() {
    SlipStashTheme {
        AddExpenseContent(
            state = AddExpenseUiState(error = "Enter an amount"),
            onAmountChange = {}, onCategoryChange = {}, onNoteChange = {},
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}
        )
    }
}