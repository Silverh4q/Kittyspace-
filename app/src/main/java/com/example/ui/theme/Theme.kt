package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CustomCyberColorScheme = darkColorScheme(
  primary = MatrixGreen,
  secondary = CyberPink,
  tertiary = CyberCyan,
  background = SpaceBackground,
  surface = CardBackground,
  onBackground = textPrimary,
  onSurface = textPrimary,
)

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to dark hacking theme
  dynamicColor: Boolean = false, // Force custom brand theme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) CustomCyberColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
