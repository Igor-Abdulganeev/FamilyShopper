package ru.gorinih.familyshopper.ui.views

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Created by Igor Abdulganeev on 03.08.2026
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
    AgslAnimated(
        modifier = modifier,
        content = content,
        startedColor = startedColor,
        endedColor = endedColor
    )

}