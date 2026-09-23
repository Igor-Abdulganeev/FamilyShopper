package ru.gorinih.familyshopper.ui.views

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Created by Igor Abdulganeev on 03.08.2026
 */

@Composable
expect fun AgslContainer(
    modifier: Modifier = Modifier,
    isAnimate: Boolean = true,
    brush: Brush = Brush.linearGradient(),
    startedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    endedColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable BoxScope.() -> Unit = {}
)