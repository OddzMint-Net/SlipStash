package com.oddzmint.slipstash.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = Cream,
    primaryContainer = Mint,
    onPrimaryContainer = DeepTeal,
    secondary = InkMuted,
    onSecondary = Cream,
    secondaryContainer = Mint,
    onSecondaryContainer = Ink,
    onTertiary = Ink,
    tertiaryContainer = AmberTint,
    onTertiaryContainer = AmberDeep,
    error = Rust,
    onError = White,
    errorContainer = RustTint,
    onErrorContainer = RustDeep,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = Mint,
    onSurfaceVariant = InkMuted,
    outline = Slate,
    outlineVariant = Hairline,
    inverseSurface = Ink,
    inverseOnSurface = Cream,
    // Material3 1.2+; delete these 7 lines if your version doesn't have them
    surfaceDim = SandDim,
    surfaceBright = Cream,
    surfaceContainerLow = SandLow,
    surfaceContainer = Sand,
    surfaceContainerHigh = SandHigh,
    surfaceContainerHighest = Hairline,
)

private val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = Cream,
    primaryContainer = Mint,
    onPrimaryContainer = DeepTeal,
    secondary = InkMuted,
    onSecondary = Cream,
    secondaryContainer = Mint,
    onSecondaryContainer = Ink,
    onTertiary = Ink,
    tertiaryContainer = AmberTint,
    onTertiaryContainer = AmberDeep,
    error = Rust,
    onError = White,
    errorContainer = RustTint,
    onErrorContainer = RustDeep,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = Mint,
    onSurfaceVariant = InkMuted,
    outline = Slate,
    outlineVariant = Hairline,
    inverseSurface = Ink,
    inverseOnSurface = Cream,
    // Material3 1.2+; delete these 7 lines if your version doesn't have them
    surfaceDim = SandDim,
    surfaceBright = Cream,
    surfaceContainerLow = SandLow,
    surfaceContainer = Sand,
    surfaceContainerHigh = SandHigh,
    surfaceContainerHighest = Hairline,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SlipStashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}