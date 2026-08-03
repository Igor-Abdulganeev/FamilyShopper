package ru.gorinih.familyshopper.ui.views

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * обертка для использования на всех устойствах и подерживающих и нет
 */

@Composable
actual fun AgslContainer(
    modifier: Modifier,
    isAnimate: Boolean,
    brush: Brush,
    startedColor: Color,
    endedColor: Color,
    content: @Composable (BoxScope.() -> Unit)
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isAnimate) {
        AgslAnimated(
            modifier = modifier,
            content = content,
            startedColor = startedColor,
            endedColor = endedColor
        )
    } else {
        Box(
            modifier = modifier.background(brush)
        ) {
            content()
        }
    }
}