package ru.gorinih.familyshopper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.ui.theme.models.PaletteScheme
import ru.gorinih.familyshopper.ui.theme.models.Palettes
import ru.gorinih.familyshopper.ui.theme.models.ThemeType

/**
 * Created by Igor Abdulganeev on 16.07.2026
 */

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
expect fun provideDynamicColorScheme(isDynamicColor: Boolean, isDarkTheme: Boolean): ColorScheme?

@Suppress("FlowOperatorInvokedInComposition")
@Composable
fun FamilyShopperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    dataPreferenceRepository: StoreRepository? = null,
    content: @Composable () -> Unit
) {

    val paletteScheme = dataPreferenceRepository?.paletteFlow()?.map { namePalette ->
        val themeType = ThemeType.entries.firstOrNull { it.name == namePalette } ?: ThemeType.MAIN
        Palettes.palettes.firstOrNull { it.themeType == themeType } ?: Palettes.instance()
    }?.collectAsState(PaletteScheme())

    val currentPalette = paletteScheme?.value ?: PaletteScheme()
    val isDynamicColorScheme =
        provideDynamicColorScheme(isDynamicColor = dynamicColor, isDarkTheme = darkTheme)

    val colorScheme = isDynamicColorScheme ?: when {
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