package ru.gorinih.familyshopper.ui.views

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativePaint

/**
 * Created by Igor Abdulganeev on 27.07.2026
 */

actual fun configurePlatformBlur(paint: Paint, radiusPx: Float) {
    paint.nativePaint.maskFilter = BlurMaskFilter(radiusPx, BlurMaskFilter.Blur.NORMAL)
}

actual fun applyPlatformShadowLayer(
    paint: Paint,
    radiusPx: Float,
    dx: Float,
    dy: Float,
    transparentColor: Int,
    color: Int
) {
    paint.nativePaint.color = transparentColor
    paint.nativePaint.setShadowLayer(
        radiusPx,
        dx,
        dy,
        color
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
actual fun allowDynamic(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
