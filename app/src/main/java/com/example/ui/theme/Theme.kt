package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val P3DarkColorScheme = darkColorScheme(
  primary = P3ElectricCyan,
  onPrimary = P3VoidBlack,
  primaryContainer = P3DeepNavy,
  onPrimaryContainer = P3IceBlue,
  secondary = P3SoftCyan,
  onSecondary = P3VoidBlack,
  secondaryContainer = P3SurfaceNavy,
  onSecondaryContainer = P3IceBlue,
  tertiary = P3SeesYellow,
  onTertiary = P3VoidBlack,
  background = P3VoidBlack,
  onBackground = P3IceBlue,
  surface = P3TartarusDark,
  onSurface = Color.White,
  surfaceVariant = P3DeepNavy,
  onSurfaceVariant = P3SilverGrey,
  outline = P3ElectricCyan,
  outlineVariant = P3SurfaceHighlight,
  error = P3ShadowRed,
  onError = Color.White
)

@Composable
fun Persona3FESTheme(
  darkTheme: Boolean = true, // Persona 3 FES is always Dark Hour inspired
  content: @Composable () -> Unit
) {
  val colorScheme = P3DarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = P3VoidBlack.toArgb()
        window.navigationBarColor = P3VoidBlack.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
