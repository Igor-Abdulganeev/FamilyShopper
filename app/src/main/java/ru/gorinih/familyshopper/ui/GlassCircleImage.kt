package ru.gorinih.familyshopper.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

class GlassCircleImage(
    private val color: Color
) : Painter() {
    override val intrinsicSize: Size = Size(100f, 100f)

    override fun DrawScope.onDraw() {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.6f),
                    color.copy(alpha = 0.3f)
                ),
                center = center,
                radius = radius
            ),
        )
        drawCircle(
            brush = Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                start = Offset(center.x / 2, center.y - radius),
                end = center
            ),
            radius = radius * 0.9f
        )
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(0.5f),
                    color.copy(alpha = 0.4f)
                ),
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height)
            ),
            style = Stroke(2f)
        )
    }
}