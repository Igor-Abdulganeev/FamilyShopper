package ru.gorinih.familyshopper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MediumGreen,
    onPrimary = White,
    secondary = LightBlue,
    onSecondary = Black,
    tertiary = DarkBlue,
    onTertiary = White,
    background = VeryDarkGray,
    onBackground = VeryLightGray,
    surface = SurfaceDark,
    onSurface = White,
    error = ErrorPink,
    onError = Black,
    primaryContainer = DarkGray,
    onPrimaryContainer = White,
    secondaryContainer = DarkGray,
    onSecondaryContainer = White,
    tertiaryContainer = DarkGray,
    onTertiaryContainer = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGrayText,
    outline = LightGray,
    inverseOnSurface = VeryDarkGray,
    scrim = White
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = Black,
    secondary = DarkBlue,
    onSecondary = White,
    tertiary = LightBlue,
    onTertiary = Black,
    background = VeryLightGray,
    onBackground = DarkGrayText,
    surface = LightGray,
    onSurface = Black,
    error = ErrorRed,
    onError = White,
    primaryContainer = LightGray,
    onPrimaryContainer = Black,
    secondaryContainer = LightGray,
    onSecondaryContainer = Black,
    tertiaryContainer = LightGray,
    onTertiaryContainer = Black,
    surfaceVariant = MediumGray,
    onSurfaceVariant = DarkGrayText,
    outline = LightGray,
    inverseOnSurface = White,
    scrim = Black
)

@Composable
fun FamilyShopperTheme(
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