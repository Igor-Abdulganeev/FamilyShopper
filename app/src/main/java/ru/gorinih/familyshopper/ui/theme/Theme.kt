package ru.gorinih.familyshopper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.theme.models.PaletteScheme

private val DarkColorScheme = darkColorScheme(
    primary = MediumGreen,
    onPrimary = White,

    secondary = DarkBlue,
    onSecondary = White,

    tertiary = LightBlue,
    onTertiary = Black,

    background = VeryDarkGray,
    onBackground = White,

    surface = SurfaceDark,
    onSurface = White,

    surfaceVariant = MediumGray.copy(alpha = 0.15f),
    onSurfaceVariant = LightGrayText,

    error = ErrorPink,
    onError = Black,

    primaryContainer = SurfaceDark,
    onPrimaryContainer = White,

    secondaryContainer = SurfaceDark,
    onSecondaryContainer = White,

    tertiaryContainer = SurfaceDark,
    onTertiaryContainer = White,

    outline = OutlineDark,
    inverseOnSurface = VeryDarkGray,
    scrim = Black
)

private val LightColorScheme = lightColorScheme(
    primary = LightGreen,
    onPrimary = Black,

    secondary = DarkBlue,
    onSecondary = White,

    tertiary = LightBlue,
    onTertiary = Black,

    background = VeryLightGray,
    onBackground = Black,

    surface = LightGray,
    onSurface = Black,

    surfaceVariant = MediumGray,
    onSurfaceVariant = DarkGrayText,

    error = ErrorRed,
    onError = White,

    primaryContainer = MediumGray,
    onPrimaryContainer = Black,

    secondaryContainer = MediumGray,
    onSecondaryContainer = Black,

    tertiaryContainer = MediumGray,
    onTertiaryContainer = Black,

    outline = OutlineLight,
    inverseOnSurface = White,
    scrim = Black
)

@Composable
fun FamilyShopperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    dataStoreRepository: StorageRepository? = null,
    content: @Composable () -> Unit
) {

        val paletteScheme = dataStoreRepository?.paletteFlow()?.collectAsState(PaletteScheme())
        val currentPalette = paletteScheme?.value ?: PaletteScheme()

        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme.copy(
                primary = currentPalette.darkPrimary,
                secondary = currentPalette.secondary,
                tertiary = currentPalette.tertiary,
                background = currentPalette.darkBackground,
                surface = currentPalette.darkSurface,
            )

            else -> LightColorScheme.copy(
                primary = currentPalette.lightPrimary,
                secondary = currentPalette.secondary,
                tertiary = currentPalette.tertiary,
                background = currentPalette.lightBackground,
                surface = currentPalette.lightSurface,
            )
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
}