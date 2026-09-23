package ru.gorinih.familyshopper.ui.views

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.skiaPaint
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.ImageFilter

/**
 * Created by Igor Abdulganeev on 27.07.2026
 */

actual fun configurePlatformBlur(paint: Paint, radiusPx: Float) {
    paint.skiaPaint.imageFilter = ImageFilter.makeBlur(
        sigmaX = radiusPx,
        sigmaY = radiusPx,
        mode = FilterTileMode.DECAL
    )
}

actual fun applyPlatformShadowLayer(
    paint: Paint,
    radiusPx: Float,
    dx: Float,
    dy: Float,
    transparentColor: Int,
    color: Int
) {
    paint.skiaPaint.color = transparentColor
    if (radiusPx > 0f) {
        paint.skiaPaint.imageFilter = ImageFilter.makeDropShadow(
            dx = dx,
            dy = dy,
            sigmaX = radiusPx / 2f,
            sigmaY = radiusPx / 2f,
            color = color
        )
    }
}

actual fun allowDynamic(): Boolean = false