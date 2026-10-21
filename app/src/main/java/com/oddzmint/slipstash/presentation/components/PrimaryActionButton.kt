package com.oddzmint.slipstash.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oddzmint.slipstash.ui.theme.SlipStashTheme

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, name = "enabled button")
@Composable
private fun PrimaryActionButtonEnabledPreview() {
    SlipStashTheme {
        PrimaryActionButton(
            text = "Save",
            onClick = {},
            enabled = true
        )
    }
}

@Preview(showBackground = true, name = "disabled button")
@Composable
private fun PrimaryActionButtonDisabledPreview() {
    SlipStashTheme {
        PrimaryActionButton(
            text = "Save",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(showBackground = true, name = "dark mode button", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PrimaryActionButtonDarkPreview() {
    SlipStashTheme {
        PrimaryActionButton(
            text = "Save",
            onClick = {},
            enabled = false
        )
    }
}