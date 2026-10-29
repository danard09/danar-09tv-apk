package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DanarDarkColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = Color.Black,
  primaryContainer = GoldContainer,
  onPrimaryContainer = OnGoldContainer,
  secondary = GoldSecondary,
  onSecondary = Color.Black,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = GoldTertiary,
  tertiary = GoldTertiary,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = GoldBorder,
  outlineVariant = DarkBorder,
  error = StatusLive,
  onError = Color.White
)

@Composable
fun DanarTvTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DanarDarkColorScheme,
    typography = Typography,
    content = content
  )
}
