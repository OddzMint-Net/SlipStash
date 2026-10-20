package com.oddzmint.slipstash.presentation.add

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.oddzmint.slipstash.domain.model.Category
import com.oddzmint.slipstash.presentation.model.AddExpenseUiState
import com.oddzmint.slipstash.ui.theme.SlipStashTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                title = { Text("Add expense") },)
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

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Amount")
                OutlinedTextField(
                    value = state.amountText,
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("R ") },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = state.error != null,
                    supportingText = state.error?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Category")
                CategoryDropdown(selected = state.category, onSelect = onCategoryChange)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Note (optional)")
                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }

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

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ReceiptArea(
    path: String?,
    onTakePhoto: () -> Unit,
    onRemovePhoto: () -> Unit

) {
    val outline = MaterialTheme.colorScheme.outline
    val shape = RoundedCornerShape(12.dp)

    if (path == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(shape)
                .clickable(onClick = onTakePhoto)
                .drawBehind {
                    drawRoundRect(
                        color = outline,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f))
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Attach receipt", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Tap to take a photo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ReceiptPreview(
                path = path,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(shape)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onTakePhoto) { Text("Retake") }
                TextButton(onClick = onRemovePhoto) { Text("Remove") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: Category,
    onSelect: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.label(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
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

@Composable
private fun ReceiptPreview(path: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(
                path,
                BitmapFactory.Options().apply { inSampleSize = 2 }
            )?.asImageBitmap()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Receipt photo",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
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
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}, onBack = {}
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
            onTakePhoto = {}, onRemovePhoto = {}, onSave = {}, onBack = {}
        )
    }
}