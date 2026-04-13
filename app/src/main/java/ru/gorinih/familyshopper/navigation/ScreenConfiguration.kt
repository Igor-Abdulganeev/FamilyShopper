package ru.gorinih.familyshopper.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

/**
 * для дальнейшего расширения на большие экраны
 */

enum class ScreenLayoutType {
    SINGLE_PANE,  // Телефон / Портрет
    TWO_PANE      // Планшет / Ландшафт
}

@Composable
fun rememberScreenConfiguration(): ScreenLayoutType {
    val adaptiveInfo = currentWindowAdaptiveInfo()

    return remember(adaptiveInfo) {
        when(adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            true -> ScreenLayoutType.TWO_PANE
            false -> ScreenLayoutType.SINGLE_PANE
        }
    }
}