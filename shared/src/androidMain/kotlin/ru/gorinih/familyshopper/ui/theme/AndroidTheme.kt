package ru.gorinih.familyshopper.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Created by Igor Abdulganeev on 16.07.2026
 */

@Composable
actual fun provideDynamicColorScheme(
    isDynamicColor: Boolean,
    isDarkTheme: Boolean,
): ColorScheme? = if (
    isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
) {
    val context = LocalContext.current
    if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
} else null
