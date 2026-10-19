package com.oddzmint.slipstash.presentation.add

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.presentation.model.AddExpenseUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

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
        onSave = viewModel::onSave,
        onBack = onBack
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
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Add expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
            OutlinedTextField(
                value = state.amountText,
                onValueChange = onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                prefix = { Text("R ") },
                singleLine = true,
                isError = state.error != null,
                supportingText = state.error?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Text("Category", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = state.category == category,
                        onClick = { onCategoryChange(category) },
                        label = {
                            Text(
                                category.name.lowercase().replace('_', ' ')
                                    .replaceFirstChar { it.uppercase() }
                            )
                        }
                    )
                }
            }

            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note (optional)") },
                singleLine = true
            )

            Text("Receipt", style = MaterialTheme.typography.labelLarge)
            if (state.receiptPath == null) {
                OutlinedButton(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) {
                    Text("Take receipt photo")
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReceiptThumbnail(path = state.receiptPath)
                    Column {
                        TextButton(onClick = onTakePhoto) { Text("Retake") }
                        TextButton(onClick = onRemovePhoto) { Text("Remove") }
                    }
                }
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun ReceiptThumbnail(path: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(
                path,
                BitmapFactory.Options().apply { inSampleSize = 4 }
            )?.asImageBitmap()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Receipt photo",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddExpensePreview() {
    MaterialTheme {
        AddExpenseContent(
            state = AddExpenseUiState(amountText = "612.50"),
            onAmountChange = {}, onCategoryChange = {}, onNoteChange = {},
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}, onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun AddExpenseErrorPreview() {
    MaterialTheme {
        AddExpenseContent(
            state = AddExpenseUiState(error = "Enter an amount"),
            onAmountChange = {}, onCategoryChange = {}, onNoteChange = {},
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}, onBack = {}
        )
    }
}