package ru.gorinih.familyshopper.ui.theme.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.gorinih.familyshopper.ui.theme.DarkBlue
import ru.gorinih.familyshopper.ui.theme.LightBlue
import ru.gorinih.familyshopper.ui.theme.LightGray
import ru.gorinih.familyshopper.ui.theme.LightGreen
import ru.gorinih.familyshopper.ui.theme.MediumGreen
import ru.gorinih.familyshopper.ui.theme.SurfaceDark
import ru.gorinih.familyshopper.ui.theme.ThemeType
import ru.gorinih.familyshopper.ui.theme.VeryDarkGray
import ru.gorinih.familyshopper.ui.theme.VeryLightGray

/**
 * Created by Igor Abdulganeev on 05.05.2026
 */


@Parcelize
data class PaletteScheme(
    val themeType: ThemeType = ThemeType.MAIN,
    val lightPrimary: @RawValue Color = LightGreen,
    val darkPrimary: @RawValue Color = MediumGreen,
    val secondary: @RawValue Color = DarkBlue,
    val tertiary: @RawValue Color = LightBlue,
    val lightBackground: @RawValue Color = VeryLightGray,
    val darkBackground: @RawValue Color = VeryDarkGray,
    val lightSurface: @RawValue Color = LightGray,
    val darkSurface: @RawValue Color = SurfaceDark,
) : Parcelable {
    fun isDynamic() = this.themeType == ThemeType.SYSTEM
}

object Palettes {
    val palettes = listOf(
        PaletteScheme(
            themeType = ThemeType.SYSTEM,
            lightPrimary = LightGreen,
            darkPrimary = MediumGreen,
            secondary = DarkBlue,
            tertiary = LightBlue,
            lightBackground = VeryLightGray,
        ),

        PaletteScheme(
            themeType = ThemeType.MAIN,
            lightPrimary = LightGreen,
            darkPrimary = MediumGreen,
            secondary = DarkBlue,
            tertiary = LightBlue,
            lightBackground = VeryLightGray,
            darkBackground = VeryDarkGray,
            lightSurface = LightGray,
            darkSurface = SurfaceDark,
        ),
        PaletteScheme(
            themeType = ThemeType.BLUE,
            lightPrimary = Color(0xFFA2B1C8),
            darkPrimary = Color(0xFF575D69),
            secondary = Color(0xFF6B7888),
            tertiary = Color(0xFFBACEE3),
            lightBackground = Color(0xFFEEEFF8),
            darkBackground = Color(0xFF2E353A),
            lightSurface = Color(0xFFF6FBFD),
            darkSurface = Color(0xFF233136),
        ),
        PaletteScheme(
            themeType = ThemeType.BROWN,
            lightPrimary = Color(0xFFA28178),
            darkPrimary = Color(0xFF5F381B),
            secondary = Color(0xFF8F6960),
            tertiary = Color(0xFFCBC1B5),
            lightBackground = Color(0xFFFDF5EE),
            darkBackground = Color(0xFF211306),
            lightSurface = Color(0xFFFDF6F6),
            darkSurface = Color(0xFF362D23),
        ),
        PaletteScheme(
            themeType = ThemeType.RED,
            lightPrimary = Color(0xFFEC9292),
            darkPrimary = Color(0xFF5F1B1B),
            secondary = Color(0xFF8F6060),
            tertiary = Color(0xFFCBB5B5),
            lightBackground = Color(0xFFFDEEEE),
            darkBackground = Color(0xFF210606),
            lightSurface = Color(0xFFFDF6F6),
            darkSurface = Color(0xFF362323),
        ),
        PaletteScheme(
            themeType = ThemeType.YELLOW,
            lightPrimary = Color(0xFFE9EC92),
            darkPrimary = Color(0xFF5F5E1B),
            secondary = Color(0xFF8F8E60),
            tertiary = Color(0xFFCBCAB5),
            lightBackground = Color(0xFFFDFCEE),
            darkBackground = Color(0xFF212006),
            lightSurface = Color(0xFFFDFDF6),
            darkSurface = Color(0xFF363523),
        ),
        PaletteScheme(
            themeType = ThemeType.SEA,
            lightPrimary = Color(0xFF7BDEDE),
            darkPrimary = Color(0xFF144E50),
            secondary = Color(0xFF4E817D),
            tertiary = Color(0xFFB5CBCA),
            lightBackground = Color(0xFFE1FAFA),
            darkBackground = Color(0xFF092A29),
            lightSurface = Color(0xFFEBFCFC),
            darkSurface = Color(0xFF091C1B),
        ),
        PaletteScheme(
            themeType = ThemeType.VIOLET,
            lightPrimary = Color(0xFFA97BDE),
            darkPrimary = Color(0xFF3E1450),
            secondary = Color(0xFF764E81),
            tertiary = Color(0xFFC9B5CB),
            lightBackground = Color(0xFFF9E1FA),
            darkBackground = Color(0xFF24092A),
            lightSurface = Color(0xFFFBEBFC),
            darkSurface = Color(0xFF1A091C),
        ),
    )

    fun instance(): PaletteScheme = PaletteScheme(
        themeType = ThemeType.MAIN,
        lightPrimary = LightGreen,
        darkPrimary = MediumGreen,
        secondary = DarkBlue,
        tertiary = LightBlue,
        lightBackground = VeryLightGray,
    )

}

